/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.virtualbox.predicates;

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.testng.Assert.assertTrue;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.CreateAndInstallVm;
import org.jclouds.virtualbox.functions.LaunchMachineIfNotAlreadyRunning;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.StorageBus;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "GuestAdditionsInstallerLiveTest")
public class GuestAdditionsInstallerLiveTest extends
		BaseVirtualBoxClientLiveTest {

	private MasterSpec sourceMachineSpec;

	@Override
	@BeforeClass(groups = "live")
	public void setupClient() {
		super.setupClient();
		String sourceName = VIRTUALBOX_IMAGE_PREFIX
				+ CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass()
						.getSimpleName());

      StorageController ideController = StorageController
               .builder()
               .name("IDE Controller")
               .bus(StorageBus.IDE)
               .attachISO(0, 0, operatingSystemIso)
               .attachHardDisk(
                        HardDisk.builder().diskpath(adminDisk(sourceName)).controllerPort(0).deviceSlot(1)
                                 .autoDelete(true).build()).attachISO(1, 1, guestAdditionsIso).build();

		VmSpec sourceVmSpec = VmSpec.builder().id(sourceName).name(sourceName)
				.osTypeId("").memoryMB(512).cleanUpMode(CleanupMode.Full)
				.controller(ideController).forceOverwrite(true).build();

		Injector injector = context.utils().injector();
		Function<String, String> configProperties = injector
				.getInstance(ValueOfConfigurationKeyOrNull.class);
		IsoSpec isoSpec = IsoSpec
				.builder()
				.sourcePath(operatingSystemIso)
				.installationScript(
						configProperties.apply(
								VIRTUALBOX_INSTALLATION_KEY_SEQUENCE).replace(
								"HOSTNAME", sourceVmSpec.getVmName())).build();
		
		NetworkAdapter networkAdapter = NetworkAdapter.builder()
				.networkAttachmentType(NetworkAttachmentType.NAT)
				.tcpRedirectRule("127.0.0.1", 2222, "", 22).build();
		NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard
				.builder().addNetworkAdapter(networkAdapter).build();

		NetworkSpec networkSpec = NetworkSpec.builder()
				.addNIC(networkInterfaceCard).build();
		sourceMachineSpec = MasterSpec.builder().iso(isoSpec).vm(sourceVmSpec)
				.network(networkSpec).build();

	}

	@Test
	public void testGuestAdditionsAreInstalled() throws Exception {
		try {
			IMachine machine = getVmWithGuestAdditionsInstalled();
			machineUtils.applyForMachine(machine.getName(),
					new LaunchMachineIfNotAlreadyRunning(manager.get(),
							ExecutionType.GUI, ""));
			assertTrue(machineUtils.lockSessionOnMachineAndApply(
					machine.getName(), LockType.Shared,
					new Function<ISession, Boolean>() {
						@Override
						public Boolean apply(ISession session) {
							return session.getMachine().getGuestPropertyValue(
									"/VirtualBox/GuestAdd/Version") != null;
						}
					}));
		} finally {
			for (VmSpec spec : ImmutableSet.of(sourceMachineSpec.getVmSpec())) {
				machineController.ensureMachineHasPowerDown(spec.getVmName());
				undoVm(spec);
			}
		}

	}

	private IMachine getVmWithGuestAdditionsInstalled() {
		try {
			Injector injector = context.utils().injector();
			return injector.getInstance(CreateAndInstallVm.class).apply(
					sourceMachineSpec);
		} catch (IllegalStateException e) {
			// already created
			return manager.get().getVBox()
					.findMachine(sourceMachineSpec.getVmSpec().getVmId());
		}
	}

}