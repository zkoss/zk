/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zkoss.zel.impl.stream;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELResolver;

public class StreamELResolverImpl extends ELResolver {

    
    public Object getValue(ELContext context, Object base, Object property) {
        return null;
    }

    
    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    
    public void setValue(ELContext context, Object base, Object property,
            Object value) {
        // NO-OP
    }

    
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;
    }

    
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
            Object base) {
        return null;
    }

    
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }

    
    public Object invoke(ELContext context, Object base, Object method,
            Class<?>[] paramTypes, Object[] params) {

        if ("stream".equals(method) && params.length == 0) {
            if (base.getClass().isArray()) {
                context.setPropertyResolved(true);
                return new Stream(new ArrayIterator(base));
            } else if (base instanceof Collection) {
                context.setPropertyResolved(true);
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) base;
                return new Stream(collection.iterator());
            }
        }

        // Not for handling by this resolver
        return null;
    }


    private static class ArrayIterator implements Iterator<Object> {

        private final Object base;
        private final int size;
        private int index = 0;

        public ArrayIterator(Object base) {
            this.base = base;
            size = Array.getLength(base);
        }

        
        public boolean hasNext() {
            return size > index;
        }

        
        public Object next() {
            return Array.get(base, index++);
        }

        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
