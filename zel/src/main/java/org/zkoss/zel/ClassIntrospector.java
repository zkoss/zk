/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
package org.zkoss.zel;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Borrowed from freemarker.ext.beans.ClassIntrospector.
// https://github.com/apache/freemarker/blob/b349362cf07d4521e7d3a6ac665036a387ed8ad8/src/main/java/freemarker/ext/beans/ClassIntrospector.java
class ClassIntrospector {
	private static final boolean HAS_DEFAULT_SUPPORT = hasDefaultSupport();

	private static boolean hasDefaultSupport() {
		try {
			Method.class.getDeclaredMethod("isDefault");
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors(BeanInfo beanInfo, Class<?> clazz) throws IntrospectionException {
		PropertyDescriptor[] introspectorPDsArray = beanInfo.getPropertyDescriptors();
		List<PropertyDescriptor> introspectorPDs = introspectorPDsArray != null ? Arrays.asList(introspectorPDsArray)
				: Collections.<PropertyDescriptor>emptyList();

		if (!HAS_DEFAULT_SUPPORT) {
			// java.beans.Introspector was good enough then.
			return introspectorPDsArray;
		}

		// introspectorPDs contains each property exactly once. But as now we will search them manually too, it can
		// happen that we find the same property for multiple times. Worse, because of indexed properties, it's possible
		// that we have to merge entries (like one has the normal reader method, the other has the indexed reader
		// method), instead of just replacing them in a Map. That's why we have introduced PropertyReaderMethodPair,
		// which holds the methods belonging to the same property name. IndexedPropertyDescriptor is not good for that,
		// as it can't store two methods whose types are incompatible, and we have to wait until all the merging was
		// done to see if the incompatibility goes away.

		// This could be Map<String, PropertyReaderMethodPair>, but since we rarely need to do merging, we try to avoid
		// creating those and use the source objects as much as possible. Also note that we initialize this lazily.
		LinkedHashMap<String, Object /*PropertyReaderMethodPair|Method|PropertyDescriptor*/> mergedPRMPs = null;

		// Collect Java 8 default methods that look like property readers into mergedPRMPs:
		// (Note that java.beans.Introspector discovers non-accessible public methods, and to emulate that behavior
		// here, we don't utilize the accessibleMethods Map, which we might already have at this point.)
		for (Method method : clazz.getMethods()) {
			if (isDefaultMethod(method) && method.getReturnType() != void.class
					&& !method.isBridge()) {
				Class<?>[] paramTypes = method.getParameterTypes();
				if (paramTypes.length == 0
						|| paramTypes.length == 1 && paramTypes[0] == int.class /* indexed property reader */) {
					String propName = getBeanPropertyNameFromReaderMethodName(
							method.getName(), method.getReturnType());
					if (propName != null) {
						if (mergedPRMPs == null) {
							// Lazy initialization
							mergedPRMPs = new LinkedHashMap<String, Object>();
						}
						if (paramTypes.length == 0) {
							mergeInPropertyReaderMethod(mergedPRMPs, propName, method);
						} else { // It's an indexed property reader method
							mergeInPropertyReaderMethodPair(mergedPRMPs, propName,
									new PropertyReaderMethodPair(null, method));
						}
					}
				}
			}
		} // for clazz.getMethods()

		if (mergedPRMPs == null) {
			// We had no interfering Java 8 default methods, so we can chose the fast route.
			return introspectorPDsArray;
		}

		for (PropertyDescriptor introspectorPD : introspectorPDs) {
			mergeInPropertyDescriptor(mergedPRMPs, introspectorPD);
		}

		// Now we convert the PRMPs to PDs, handling case where the normal and the indexed read methods contradict.
		List<PropertyDescriptor> mergedPDs = new ArrayList<PropertyDescriptor>(mergedPRMPs.size());
		for (Map.Entry<String, Object> entry : mergedPRMPs.entrySet()) {
			String propName = entry.getKey();
			Object propDescObj = entry.getValue();
			if (propDescObj instanceof PropertyDescriptor) {
				mergedPDs.add((PropertyDescriptor) propDescObj);
			} else {
				Method readMethod;
				Method indexedReadMethod;
				if (propDescObj instanceof Method) {
					readMethod = (Method) propDescObj;
					indexedReadMethod = null;
				} else if (propDescObj instanceof PropertyReaderMethodPair) {
					PropertyReaderMethodPair prmp = (PropertyReaderMethodPair) propDescObj;
					readMethod = prmp.readMethod;
					indexedReadMethod = prmp.indexedReadMethod;
					if (readMethod != null && indexedReadMethod != null
							&& indexedReadMethod.getReturnType() != readMethod.getReturnType().getComponentType()) {
						// Here we copy the java.beans.Introspector behavior: If the array item class is not exactly the
						// the same as the indexed read method return type, we say that the property is not indexed.
						indexedReadMethod = null;
					}
				} else {
					throw new IntrospectionException("Internal bug: propDescObj type unexpected");
				}

				mergedPDs.add(
						indexedReadMethod != null
								? new IndexedPropertyDescriptor(propName,
									readMethod, null, indexedReadMethod, null)
								: new PropertyDescriptor(propName, readMethod, null));

			}
		}
		return mergedPDs.toArray(new PropertyDescriptor[0]);
	}

	private static boolean isDefaultMethod(Method method) {
		// Default methods are public non-abstract instance methods
		// declared in an interface.
		return ((method.getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) ==
				Modifier.PUBLIC) && method.getDeclaringClass().isInterface();
	}

	private void mergeInPropertyDescriptor(LinkedHashMap<String, Object> mergedPRMPs, PropertyDescriptor pd) {
		String propName = pd.getName();
		Object replaced = mergedPRMPs.put(propName, pd);
		if (replaced != null) {
			PropertyReaderMethodPair newPRMP = new PropertyReaderMethodPair(pd);
			putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRMP);
		}
	}

	private void mergeInPropertyReaderMethod(LinkedHashMap<String, Object> mergedPRMPs,
	                                         String propName, Method readerMethod) {
		Object replaced = mergedPRMPs.put(propName, readerMethod);
		if (replaced != null) {
			putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName,
					replaced, new PropertyReaderMethodPair(readerMethod, null));
		}
	}

	private void mergeInPropertyReaderMethodPair(LinkedHashMap<String, Object> mergedPRMPs,
	                                             String propName, PropertyReaderMethodPair newPRM) {
		Object replaced = mergedPRMPs.put(propName, newPRM);
		if (replaced != null) {
			putIfMergedPropertyReaderMethodPairDiffers(mergedPRMPs, propName, replaced, newPRM);
		}
	}

	private void putIfMergedPropertyReaderMethodPairDiffers(LinkedHashMap<String, Object> mergedPRMPs,
	                                                        String propName, Object replaced, PropertyReaderMethodPair newPRMP) {
		PropertyReaderMethodPair replacedPRMP = PropertyReaderMethodPair.from(replaced);
		PropertyReaderMethodPair mergedPRMP = PropertyReaderMethodPair.merge(replacedPRMP, newPRMP);
		if (!mergedPRMP.equals(newPRMP)) {
			mergedPRMPs.put(propName, mergedPRMP);
		}
	}

	/**
	 * Extracts the JavaBeans property from a reader method name, or returns {@code null} if the method name doesn't
	 * look like a reader method name.
	 */
	private static String getBeanPropertyNameFromReaderMethodName(String name, Class<?> returnType) {
		int start;
		if (name.startsWith("get")) {
			start = 3;
		} else if (returnType == boolean.class && name.startsWith("is")) {
			start = 2;
		} else {
			return null;
		}
		int ln = name.length();

		if (start == ln) {
			return null;
		}
		char c1 = name.charAt(start);

		return start + 1 < ln && Character.isUpperCase(name.charAt(start + 1)) && Character.isUpperCase(c1)
				? name.substring(start) // getFOOBar => "FOOBar" (not lower case) according the JavaBeans spec.
				: new StringBuilder(ln - start).append(Character.toLowerCase(c1)).append(name, start + 1, ln)
				.toString();
	}

	private static class PropertyReaderMethodPair {
		private final Method readMethod;
		private final Method indexedReadMethod;

		PropertyReaderMethodPair(Method readerMethod, Method indexedReaderMethod) {
			this.readMethod = readerMethod;
			this.indexedReadMethod = indexedReaderMethod;
		}

		PropertyReaderMethodPair(PropertyDescriptor pd) {
			this(
					pd.getReadMethod(),
					pd instanceof IndexedPropertyDescriptor
							? ((IndexedPropertyDescriptor) pd).getIndexedReadMethod() : null);
		}

		static PropertyReaderMethodPair from(Object obj) {
			if (obj instanceof PropertyReaderMethodPair) {
				return (PropertyReaderMethodPair) obj;
			} else if (obj instanceof PropertyDescriptor) {
				return new PropertyReaderMethodPair((PropertyDescriptor) obj);
			} else if (obj instanceof Method) {
				return new PropertyReaderMethodPair((Method) obj, null);
			} else {
				throw new IllegalArgumentException("Unexpected obj type: " + obj.getClass().getName());
			}
		}

		static PropertyReaderMethodPair merge(PropertyReaderMethodPair oldMethods, PropertyReaderMethodPair newMethods) {
			return new PropertyReaderMethodPair(
					newMethods.readMethod != null ? newMethods.readMethod : oldMethods.readMethod,
					newMethods.indexedReadMethod != null ? newMethods.indexedReadMethod
							: oldMethods.indexedReadMethod);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((indexedReadMethod == null) ? 0 : indexedReadMethod.hashCode());
			result = prime * result + ((readMethod == null) ? 0 : readMethod.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			PropertyReaderMethodPair other = (PropertyReaderMethodPair) obj;
			return other.readMethod == readMethod && other.indexedReadMethod == indexedReadMethod;
		}
	}
}
