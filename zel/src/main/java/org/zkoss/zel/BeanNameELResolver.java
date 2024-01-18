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
package org.zkoss.zel;

import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @since EL 3.0
 */
public class BeanNameELResolver extends ELResolver {

    private final BeanNameResolver beanNameResolver;

    public BeanNameELResolver(BeanNameResolver beanNameResolver) {
        this.beanNameResolver = beanNameResolver;
    }

    
    public Object getValue(ELContext context, Object base, Object property) {

        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null || !(property instanceof String)) {
            return null;
        }

        String beanName = (String) property;
        if (isNameResolved(context, beanName)) { //ZK-5418, cache bean name and value (cache store in ELContext)
            try {
                Object result = getBean(context, (String) property); //ZK-5418
                context.setPropertyResolved(base, property);
                return result;
            } catch (Throwable t) {
                Util.handleThrowable(t);
                throw new ELException(t);
            }
        }

        return null;
    }

    
    public void setValue(ELContext context, Object base, Object property,
            Object value) {

        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null || !(property instanceof String)) {
            return;
        }

        String beanName = (String) property;

        boolean isResolved = context.isPropertyResolved();

        boolean isReadOnly;
        try {
            isReadOnly = isReadOnly(context, base, property);
        } catch (Throwable t) {
            Util.handleThrowable(t);
            throw new ELException(t);
        } finally {
            context.setPropertyResolved(isResolved);
        }

        if (isReadOnly) {
            throw new PropertyNotWritableException(Util.message(context,
                    "beanNameELResolver.beanReadOnly", beanName));
        }

        if (isNameResolved(context, beanName) || //ZK-5418, cache bean name and value (cache store in ELContext)
                beanNameResolver.canCreateBean(beanName)) {
            try {
                setBeanValue(context, beanName, value); //ZK-5418
                context.setPropertyResolved(base, property);
            } catch (Throwable t) {
                Util.handleThrowable(t);
                throw new ELException(t);
            }
        }
    }

    
    public Class<?> getType(ELContext context, Object base, Object property) {

        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null || !(property instanceof String)) {
            return null;
        }

        String beanName = (String) property;

        try {
            if (isNameResolved(context, beanName)) { //ZK-5418, cache bean name and value (cache store in ELContext)
                Object bean = getBean(context, beanName);
                if (bean == null) return null;
                Class<?> result = bean.getClass();
                context.setPropertyResolved(base, property);
                return result;
            }
        } catch (Throwable t) {
            Util.handleThrowable(t);
            throw new ELException(t);
        }

        return null;
    }

    
    public boolean isReadOnly(ELContext context, Object base, Object property) {

        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null || !(property instanceof String)) {
            // Return value undefined
            return false;
        }

        String beanName = (String) property;

        if (isNameResolved(context, beanName)) { //ZK-5418, cache bean name and value (cache store in ELContext)
            boolean result;
            try {
                result = beanNameResolver.isReadOnly(beanName);
            } catch (Throwable t) {
                Util.handleThrowable(t);
                throw new ELException(t);
            }
            context.setPropertyResolved(base, property);
            return result;
        }

        // Return value undefined
        return false;
    }

    
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
            Object base) {
        return null;
    }

    
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return String.class;
    }

    //ZK-5418, cache bean name and value (cache in ELContext)
    private boolean isNameResolved(ELContext context, String beanName) {
        Map<String,Object> cache = (Map<String, Object>) context.getContext(beanNameResolver.getClass());
        return cache != null && cache.containsKey(beanName);
    }

    private Object getBean(ELContext context, String beanName) {
        Map<String,Object> cache = (Map<String, Object>) context.getContext(beanNameResolver.getClass());
        return cache != null ? cache.get(beanName) : null;
    }

    private void setBeanValue(ELContext context, String beanName, Object value)
            throws PropertyNotWritableException {
        Class key = beanNameResolver.getClass();
        Map<String,Object> cache = (Map<String, Object>) context.getContext(key);
        if (cache == null) {
            cache = new HashMap<>();
            context.putContext(key, cache);
        }
        cache.put(beanName, value);
    }
}
