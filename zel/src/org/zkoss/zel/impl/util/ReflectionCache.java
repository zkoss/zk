package org.zkoss.zel.impl.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionCache {

	private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
	
	private static final Map<Class<?>, Map<String, Method[]>> SETTERS_METHODS_CACHE = new ConcurrentHashMap<Class<?>, Map<String,Method[]>>();
	
	public static Method[] getSetter(Class<?> clazz, String propertyName) {
		Map<String, Method[]> classSetters = SETTERS_METHODS_CACHE.get(clazz);
		
		if (classSetters != null) {
			Method[] methods = classSetters.get(propertyName);
			if (methods != null) {
				return methods;
			}
		}
		
		return populateSetters(clazz, propertyName);
	}
	
	private static Method[] populateSetters(Class<?> clazz, String propertyName) {
		String setterName = "";
		if(propertyName != null && propertyName.length()>0){
			setterName = new StringBuilder("set")
					.append(propertyName.substring(0,1).toUpperCase(Locale.ENGLISH))
					.append(propertyName.substring(1))
					.toString();
		}
		
		List<Method> resultList = null;
		
		for (Method method: clazz.getMethods()) {
			if (method.getName().equals(setterName)) {
				Class<?>[] clazzes = method.getParameterTypes();
				
				if (clazzes.length != 1) {
					continue;
				}
				
				if (resultList == null) {
					resultList = new ArrayList<Method>();
				}
				
				resultList.add(method);
			}
		}
		
		Method[] result = EMPTY_METHOD_ARRAY;
		if (resultList != null && !resultList.isEmpty()) {
			result = resultList.toArray(new Method[resultList.size()]);
		}
		
		Map<String, Method[]> classMap = SETTERS_METHODS_CACHE.get(clazz);
		
		if (classMap == null) {
			classMap = new ConcurrentHashMap<String, Method[]>();
			SETTERS_METHODS_CACHE.put(clazz, classMap);
		}
		
		classMap.put(propertyName, result);
		
		return result;
	}
	
}
