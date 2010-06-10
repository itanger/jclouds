/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.concurrent;

import java.util.concurrent.TimeoutException;

import org.jclouds.util.Utils;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;

/**
 * 
 * @author Adrian Cole
 */
public class RetryOnTimeOutExceptionSupplier<T> implements Supplier<T> {
   private final Supplier<T> delegate;

   public RetryOnTimeOutExceptionSupplier(Supplier<T> delegate) {
      this.delegate = delegate;
   }

   @Override
   public T get() {
      TimeoutException ex = null;
      for (int i = 0; i < 3; i++) {
         try {
            ex = null;
            return delegate.get();
         } catch (Exception e) {
            if ((ex = Utils.getFirstThrowableOfType(e, TimeoutException.class)) != null)
               continue;
            Throwables.propagate(e);
            assert false;
            return null;
         }
      }
      if (ex != null)
         Throwables.propagate(ex);
      assert false;
      return null;
   }

}