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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since EL 3.0
 */
public class ImportHandler {

    private List<String> packages = new ArrayList<String>();
    private Map<String,Class<?>> clazzes = new HashMap<String,Class<?>>();
    private Map<String,Class<?>> statics = new HashMap<String,Class<?>>();
    private PageClassResolver pageClassResolver;

    public ImportHandler() {
        importPackage("java.lang");
    }

    public void setPageClassResolver(PageClassResolver pageClassResolver) {
        this.pageClassResolver = pageClassResolver;
    }

    public void importStatic(String name) throws org.zkoss.zel.ELException {
        int lastPeriod = name.lastIndexOf('.');

        if (lastPeriod < 0) {
            throw new ELException(Util.message(
                    null, "importHandler.invalidStaticName", name));
        }

        String className = name.substring(0, lastPeriod);
        String fieldOrMethodName = name.substring(lastPeriod + 1);

        Class<?> clazz = findClass(className);

        if (clazz == null) {
            throw new ELException(Util.message(
                    null, "importHandler.invalidClassNameForStatic",
                    className, name));
        }

        boolean found = false;

        for (Field field : clazz.getFields()) {
            if (field.getName().equals(fieldOrMethodName)) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) &&
                        Modifier.isPublic(modifiers)) {
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(fieldOrMethodName)) {
                    int modifiers = method.getModifiers();
                    if (Modifier.isStatic(modifiers) &&
                            Modifier.isPublic(modifiers)) {
                        found = true;
                        break;
                    }
                }
            }
        }

        if (!found) {
            throw new ELException(Util.message(null,
                    "importHandler.staticNotFound", fieldOrMethodName,
                    className, name));
        }

        Class<?> conflict = statics.get(fieldOrMethodName);
        if (conflict != null) {
            throw new ELException(Util.message(null,
                    "importHandler.ambiguousStaticImport", name,
                    conflict.getName() + '.' +  fieldOrMethodName));
        }

        statics.put(fieldOrMethodName, clazz);
    }


    public void importClass(String name) throws org.zkoss.zel.ELException {
        if (!name.contains(".")) {
            throw new ELException(Util.message(
                    null, "importHandler.invalidClassName", name));
        }

        Class<?> clazz = findClass(name);

        if (clazz == null) {
            throw new ELException(Util.message(
                    null, "importHandler.classNotFound", name));
        }

        String simpleName = clazz.getSimpleName();
        Class<?> conflict = clazzes.get(simpleName);

        if (conflict == null || NotFound.class.equals(conflict)) {
            // No conflict - add it
            clazzes.put(simpleName, clazz);
        } else {
            // Check for a duplicate
            if (conflict.equals(clazz)) {
                // This is a duplicate.
                // NO-OP
            } else {
                throw new ELException(Util.message(null,
                        "importHandler.ambiguousImport", name, conflict.getName()));
            }
        }
    }


    public void importPackage(String name) {
        // Import ambiguity is handled at resolution, not at import
        // Whether the package exists is not checked,
        // a) for sake of performance when used in JSPs (BZ 57142),
        // b) java.lang.Package.getPackage(name) is not reliable (BZ 57574),
        // c) such check is not required by specification.
        packages.add(name);
    }


    public java.lang.Class<?> resolveClass(String name) {
        if (name == null || name.contains(".")) {
            return null;
        }

        Class<?> result = clazzes.get(name);

        if (result != null) {
            if (NotFound.class.equals(result)) {
                return null;
            } else {
                return result;
            }
        }

        // Search the package imports - note there may be multiple matches
        // (which correctly triggers an error)
        for (String p : packages) {
            String className = p + '.' + name;
            Class<?> clazz = findClass(className);
            if (clazz != null) {
                if (result != null) {
                    throw new ELException(Util.message(null,
                            "importHandler.ambiguousImport", className,
                            result.getName()));
                }
                result = clazz;
            }
        }

        // (Potix) Try Page ClassResolver as a last resort
        if (result == null && pageClassResolver != null) {
            try {
                result = pageClassResolver.resolveClass(name);
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (result == null) {
            // Cache NotFound results to save repeated calls to findClass()
            // which is relatively slow
            clazzes.put(name, NotFound.class);
        } else {
            clazzes.put(name, result);
        }

        return result;
    }


    public java.lang.Class<?> resolveStatic(String name) {
        return statics.get(name);
    }


    private Class<?> findClass(String name) {
        Class<?> clazz;
        try {
             clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }

        // Class must be public, non-abstract and not an interface
        int modifiers = clazz.getModifiers();
        if (!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers) ||
                Modifier.isInterface(modifiers)) {
            throw new ELException(Util.message(
                    null, "importHandler.invalidClass", name));
        }

        return clazz;
    }

    /*
     * Marker class used because null values are not permitted in a
     * ConcurrentHashMap.
     */
    private static class NotFound {
    }

    /*
     * (Potix) Resolve class from current Page scope (e.g. <?import ?>) or zscript.
     */
    public interface PageClassResolver {
        /**
         * Resolves the class of the specified name.
         * @exception ClassNotFoundException if the class is not found.
         */
        Class<?> resolveClass(String clsnm) throws ClassNotFoundException;
    }
}
