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
package org.jclouds.openstack.nova.v1_1.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.domain.SecurityGroup;
import org.jclouds.openstack.nova.v1_1.domain.SecurityGroupRule;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaClientExpectTest;
import org.jclouds.openstack.nova.v1_1.parse.ParseSecurityGroupListTest;
import org.jclouds.openstack.nova.v1_1.parse.ParseSecurityGroupTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code SecurityGroupAsyncClient}
 * 
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "SecurityGroupClientExpectTest")
public class SecurityGroupClientExpectTest extends BaseNovaClientExpectTest {
   public void testListSecurityGroupsWhenResponseIs2xx() throws Exception {
      HttpRequest listSecurityGroups = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/os-security-groups"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listSecurityGroupsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/securitygroup_list.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listSecurityGroups,
            listSecurityGroupsResponse);

      assertEquals(clientWhenSecurityGroupsExist.getConfiguredRegions(), ImmutableSet.of("North"));

      assertEquals(clientWhenSecurityGroupsExist.getSecurityGroupExtensionForRegion("North").get().listSecurityGroups()
            .toString(), new ParseSecurityGroupListTest().expected().toString());
   }

   public void testListSecurityGroupsWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listListSecurityGroups = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/os-security-groups"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse listListSecurityGroupsResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoSecurityGroupsExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, listListSecurityGroups,
            listListSecurityGroupsResponse);

      assertTrue(clientWhenNoSecurityGroupsExist.getSecurityGroupExtensionForRegion("North").get().listSecurityGroups()
            .isEmpty());
   }

   public void testGetSecurityGroupWhenResponseIs2xx() throws Exception {

      HttpRequest getSecurityGroup = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/os-security-groups/0"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/securitygroup_details.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, getSecurityGroup,
            getSecurityGroupResponse);

      assertEquals(clientWhenSecurityGroupsExist.getSecurityGroupExtensionForRegion("North").get()
            .getSecurityGroup("0").toString(), new ParseSecurityGroupTest().expected().toString());
   }

   public void testGetSecurityGroupWhenResponseIs404() throws Exception {
      HttpRequest getSecurityGroup = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/os-security-groups/0"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build()).build();

      HttpResponse getSecurityGroupResponse = HttpResponse.builder().statusCode(404).build();

      NovaClient clientWhenNoSecurityGroupsExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, getSecurityGroup,
            getSecurityGroupResponse);

      assertNull(clientWhenNoSecurityGroupsExist.getSecurityGroupExtensionForRegion("North").get()
            .getSecurityGroup("0"));

   }

   public void testCreateSecurityGroupWhenResponseIs2xx() throws Exception {
      HttpRequest createSecurityGroup = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/os-security-groups"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build())
            .payload(
                  payloadFromStringWithContentType(
                        "{\"security_group\":{\"name\":\"name\",\"description\":\"description\"}}", "application/json"))
            .build();

      HttpResponse createSecurityGroupResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/securitygroup_created.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createSecurityGroup,
            createSecurityGroupResponse);

      assertEquals(
            clientWhenSecurityGroupsExist.getSecurityGroupExtensionForRegion("North").get()
                  .createSecurityGroup("name", "description").toString(), createSecurityGroupExpected().toString());
   }

   public void testDeleteSecurityGroupWhenResponseIs2xx() throws Exception {
      HttpRequest deleteSecurityGroup = HttpRequest
            .builder()
            .method("DELETE")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/os-security-groups/160"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "*/*").put("X-Auth-Token", authToken)
                        .build()).build();

      HttpResponse deleteSecurityGroupResponse = HttpResponse.builder().statusCode(202).build();

      NovaClient clientWhenServersExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, deleteSecurityGroup,
            deleteSecurityGroupResponse);

      assertTrue(clientWhenServersExist.getSecurityGroupExtensionForRegion("North").get().deleteSecurityGroup("160"));

   }

   public void testCreateSecurityGroupRuleWhenResponseIs2xx() throws Exception {
      HttpRequest createSecurityGroupRule = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/os-security-group-rules"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("X-Auth-Token", authToken).build())
            .payload(
                  payloadFromStringWithContentType(
                        "{\"security_group_rule\":{\"ip_protocol\":\"tcp\",\"from_port\":\"80\",\"to_port\":\"8080\",\"cidr\":\"0.0.0.0/0\",\"group_id\":\"\",\"parent_group_id\":\"161\"}}",
                        "application/json")).build();

      HttpResponse createSecurityGroupRuleResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/securitygrouprule_created.json")).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, createSecurityGroupRule,
            createSecurityGroupRuleResponse);

      assertEquals(clientWhenSecurityGroupsExist.getSecurityGroupExtensionForRegion("North").get()
            .createSecurityGroupRule("tcp", "80", "8080", "0.0.0.0/0", "", "161").toString(),
            createSecurityGroupRuleExpected().toString());
   }

   public void testDeleteSecurityGroupRuleWhenResponseIs2xx() throws Exception {
      HttpRequest deleteSecurityGroupRule = HttpRequest
            .builder()
            .method("DELETE")
            .endpoint(URI.create("https://compute.north.host/v1.1/3456/os-security-group-rules/161"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "*/*").put("X-Auth-Token", authToken)
                        .build()).build();

      HttpResponse deleteSecurityGroupRuleResponse = HttpResponse.builder().statusCode(202).build();

      NovaClient clientWhenSecurityGroupsExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKey,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse, deleteSecurityGroupRule,
            deleteSecurityGroupRuleResponse);

      assertTrue(clientWhenSecurityGroupsExist.getSecurityGroupExtensionForRegion("North").get()
            .deleteSecurityGroupRule("161"));

   }

   private SecurityGroup createSecurityGroupExpected() {
      return SecurityGroup.builder().description("description").id("160").name("name")
            .rules(ImmutableSet.<SecurityGroupRule> of()).tenantId("dev_16767499955063").build();
   }

   private SecurityGroupRule createSecurityGroupRuleExpected() {
      return SecurityGroupRule.builder().fromPort(80).group(ImmutableMap.<String, String> of()).id("218")
            .ipProtocol(SecurityGroupRule.IpProtocol.TCP).ipRange(ImmutableMap.of("cidr", "0.0.0.0/0"))
            .parentGroupId("161").toPort(8080).build();
   }

}
