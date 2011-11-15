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
package org.jclouds.cloudstack.features;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.ExtractMode;
import org.jclouds.cloudstack.domain.ISO;
import org.jclouds.cloudstack.domain.ISOPermissions;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.DeleteISOOptions;
import org.jclouds.cloudstack.options.ExtractISOOptions;
import org.jclouds.cloudstack.options.ListISOsOptions;
import org.jclouds.cloudstack.options.RegisterISOOptions;
import org.jclouds.cloudstack.options.UpdateISOOptions;
import org.jclouds.cloudstack.options.UpdateISOPermissionsOptions;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Unwrap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * 
 * <p/>
 * 
 * @see ISOClient
 * @see http://download.cloud.com/releases/2.2.12/api/TOC_User.html
 * @author Richard Downer
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
@SkipEncoding({'/', ','})
public interface ISOAsyncClient {

   /**
    * Attaches an ISO to a virtual machine.
    *
    * @param isoId the ID of the ISO file
    * @param vmId the ID of the virtual machine
    * @return an asynchronous job response.
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "attachISO")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> attachISO(@QueryParam("id") long isoId, @QueryParam("virtualmachineid") long vmId);

   /**
    * Detaches any ISO file (if any) currently attached to a virtual machine.
    *
    * @param vmId The ID of the virtual machine
    * @return an asynchronous job response.
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "detachISO")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> detachISO(@QueryParam("virtualmachineid") long vmId);

   /**
    * Gets information about an ISO by its ID.
    *
    * @param id the ID of the ISO file
    * @return the ISO object matching the ID
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "listISOs")
   @Unwrap
   ListenableFuture<ISO> getISO(@QueryParam("id") long id);

   /**
    * Lists all available ISO files.
    *
    * @param options optional arguments
    * @return a set of ISO objects the match the filter
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "listISOs")
   @Unwrap
   ListenableFuture<Set<ISO>> listISOs(ListISOsOptions... options);

   /**
    * Registers an existing ISO into the Cloud.com Cloud.
    *
    * @param name the name of the ISO
    * @param displayText the display text of the ISO. This is usually used for display purposes.
    * @param url the URL to where the ISO is currently being hosted
    * @param zoneId the ID of the zone you wish to register the ISO to.
    * @param options optional arguments
    * @return the newly-added ISO
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "registerISO")
   @Unwrap
   ListenableFuture<ISO> registerISO(@QueryParam("name") String name, @QueryParam("displaytext") String displayText, @QueryParam("url") String url, @QueryParam("zoneid") long zoneId, RegisterISOOptions... options);

   /**
    * 
    *
    * @param id the ID of the ISO file
    * @param options optional arguments
    * @return the ISO object matching the ID
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "updateISO")
   @Unwrap
   ListenableFuture<ISO> updateISO(@QueryParam("id") long id, UpdateISOOptions... options);

   /**
    * Deletes an ISO file.
    *
    * @param id the ID of the ISO file
    * @param options optional arguments
    * @return an asynchronous job response.
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "deleteISO")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> deleteISO(@QueryParam("id") long id, DeleteISOOptions... options);

   /**
    * Copies a template from one zone to another.
    *
    * @param isoId Template ID.
    * @param sourceZoneId ID of the zone the template is currently hosted on.
    * @param destZoneId ID of the zone the template is being copied to.
    * @return an asynchronous job response.
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "copyISO")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> copyISO(@QueryParam("id") long isoId, @QueryParam("sourcezoneid") long sourceZoneId, @QueryParam("destzoneid") long destZoneId);

   /**
    * Updates iso permissions
    *
    * @param id the template ID
    * @param options optional arguments
    * @return 
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "updateISOPermissions")
   @Unwrap
   ListenableFuture<Void> updateISOPermissions(@QueryParam("id") long id, UpdateISOPermissionsOptions... options);

   /**
    * List template visibility and all accounts that have permissions to view this template.
    *
    * @param id the template ID
    * @param options optional arguments
    * @return A set of the permissions on this ISO
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "listISOPermissions")
   @Unwrap
   ListenableFuture<Set<ISOPermissions>> listISOPermissions(@QueryParam("id") long id, AccountInDomainOptions... options);

   /**
    * Extracts an ISO
    *
    * @param id the ID of the ISO file
    * @param mode the mode of extraction - HTTP_DOWNLOAD or FTP_UPLOAD
    * @param zoneId the ID of the zone where the ISO is originally located
    * @param options optional arguments
    * @return an asynchronous job response.
    */
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @QueryParams(keys = "command", values = "extractISO")
   @Unwrap
   ListenableFuture<AsyncCreateResponse> extractISO(@QueryParam("id") long id, @QueryParam("mode") ExtractMode mode, @QueryParam("zoneid") long zoneId, ExtractISOOptions... options);

}