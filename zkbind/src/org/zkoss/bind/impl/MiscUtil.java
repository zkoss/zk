/* MiscUtil.java

	Purpose:
		
	Description:
		
	History:
		2012/4/12 Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.UiException;

/**
 * internal use only misc util
 * 
 * @author dennis
 * 
 */
public class MiscUtil {

	public static String toSimpleMethodSignature(Method method) {
		// only keep the method name and parameter type
		StringBuilder sb = new StringBuilder();

		sb.append(method.getName() + "(");
		Class<?>[] params = method.getParameterTypes();
		for (int j = 0; j < params.length; j++) {
			sb.append(getTypeName(params[j]));
			if (j < (params.length - 1))
				sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	static String getTypeName(Class type) {
		if (type.isArray()) {
			try {
				Class cl = type;
				int dimensions = 0;
				while (cl.isArray()) {
					dimensions++;
					cl = cl.getComponentType();
				}
				StringBuilder sb = new StringBuilder();
				sb.append(cl.getName());
				for (int i = 0; i < dimensions; i++) {
					sb.append("[]");
				}
				return sb.toString();
			} catch (Throwable e) { /* FALLTHRU */
			}
		}
		return type.getName();
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstanceFromProperty(String key,String def,Class<T> type){
		String clz = Library.getProperty(key,def);
		if(clz!=null){
			final Object v;
			try {
				v = Classes.newInstanceByThread(clz);
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex);
			}
			
			if (!type.isAssignableFrom(v.getClass()))
				throw new UiException(type + " must be implemented by "+v);
			return (T)v;
		}
		return null;
	}
}
