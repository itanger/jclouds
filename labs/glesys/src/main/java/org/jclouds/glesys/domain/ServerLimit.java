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
package org.jclouds.glesys.domain;

import com.google.common.base.Objects;

/**
 * Detailed information about an OpenVZ server's limits
 * 
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_limits" />
 */
public class ServerLimit {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int held;
      private int maxHeld;
      private int barrier;
      private int limit;
      private int failCount;

      public Builder held(int held) {
         this.held = held;
         return this;
      }

      public Builder maxHeld(int maxHeld) {
         this.maxHeld = maxHeld;
         return this;
      }

      public Builder barrier(int barrier) {
         this.barrier = barrier;
         return this;
      }

      public Builder limit(int limit) {
         this.limit = limit;
         return this;
      }

      public Builder failCount(int failCount) {
         this.failCount = failCount;
         return this;
      }

      public ServerLimit build() {
         return new ServerLimit(held, maxHeld, barrier, limit, failCount);
      }
   }

   private final long held;
   private final long maxHeld;
   private final long barrier;
   private final long limit;
   private final long failCount;

   public ServerLimit(long held, long maxHeld, long barrier, long limit, long failCount) {
      this.held = held;
      this.maxHeld = maxHeld;
      this.barrier = barrier;
      this.limit = limit;
      this.failCount = failCount;
   }

   public long getHeld() {
      return held;
   }

   public long getMaxHeld() {
      return maxHeld;
   }

   public long getBarrier() {
      return barrier;
   }

   public long getLimit() {
      return limit;
   }

   public long getFailCount() {
      return failCount;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ServerLimit) {
         final ServerLimit other = (ServerLimit) object;
         return Objects.equal(held, other.held)
               && Objects.equal(maxHeld, other.maxHeld)
               && Objects.equal(barrier, other.barrier)
               && Objects.equal(limit, other.limit)
               && Objects.equal(failCount, other.failCount);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(held, maxHeld, barrier, limit, failCount);
   }

   @Override
   public String toString() {
      return String.format("[held=%d, maxHeld=%d, barrier=%d, limit=%d, failCount=%d]", held, maxHeld, barrier, limit, failCount);
   }
}
