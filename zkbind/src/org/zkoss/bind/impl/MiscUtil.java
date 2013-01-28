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
import org.zkoss.util.resource.Location;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;

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
	
	
	public static String formatLocationMessage(String message,Object obj){
		if(obj==null) return message;
		if(obj instanceof Component){
			return formatLocationMessage(message,(Component)obj);
		}else if(obj instanceof Annotation){
			return formatLocationMessage(message,(Annotation)obj);
		}else if(obj instanceof Location){
			return formatLocationMessage(message,(Location)obj);
		}else{
			return formatLocationMessage(message,(Location)null);
		}
	}
	
	public static String formatLocationMessage(String message,Component comp){
		if(comp==null) return message;
		return formatLocationMessage(message,toComponentLocation(comp));
	}

	private static Location toComponentLocation(Component comp) {
		//TODO a better way to get it without break any compatibility (use getLocation in Component/ComponentCtrl is too strong
		if(comp instanceof AbstractComponent){
			try {
				//this implement is very easy to be break in future version.
				Field field = AbstractComponent.class.getField("_loc");
				field.setAccessible(true);
				return (Location)field.get(comp);
			} catch (Exception x) {}
		}
		return null;
	}

	public static String formatLocationMessage(String message,Annotation anno){
		if(anno==null) return message;
		return formatLocationMessage(message,anno.getLocation());
	}

	public static String formatLocationMessage(String message,Location loc){
		if(loc==null) return message;
		String path = loc.getPath();
		int ln = loc.getLineNumber();
		int cn = loc.getColumnNumber();
		StringBuilder sb = new StringBuilder();
		if(message!=null){
			sb.append(message);
		}
		sb.append(" at [").append(path);
		if(ln>=0){
			sb.append(", line:").append(ln);
			if(cn>=0){
				sb.append(", col: ").append(cn);
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
