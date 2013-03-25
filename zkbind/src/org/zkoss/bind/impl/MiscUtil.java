/* MiscUtil.java

	Purpose:
		
	Description:
		
	History:
		2012/4/12 Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.impl;

import java.lang.reflect.Method;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.Location;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.Annotation;

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
		return formatLocationMessage(message,toComponentLocation(comp),false);
	}

	private static Location toComponentLocation(Component comp) {
		if(comp instanceof AbstractComponent){
			Annotation anno = ((AbstractComponent)comp).getAnnotation(null, "ZKLOC");
			return anno==null?null:anno.getLocation();
		}
		return null;
	}

	public static String formatLocationMessage(String message,Annotation anno){
		if(anno==null) return message;
		return formatLocationMessage(message,anno.getLocation(),true);
	}

	public static String formatLocationMessage(String message,Location loc){
		return formatLocationMessage(message,loc,true);
	}
	
	private static String formatLocationMessage(String message,Location loc, boolean showColumn){
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
			if(showColumn && cn>=0){
				sb.append(", nearby column: ").append(cn);
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	//utility to prevent nested location info in exception
	public static RuntimeException mergeExceptionInfo(Exception ex,Object loc){
		Location location = null;
		boolean showColumn = true;
		if(loc instanceof Component){
			location = toComponentLocation((Component)loc);
			showColumn = false;
		}else if(loc instanceof Annotation){
			location = ((Annotation)loc).getLocation();
		}else if(loc instanceof Location){
			location = (Location)loc;
		}
		
		if(location==null){
			if(ex instanceof RuntimeException){
				return (RuntimeException) ex;
			}else{
				return new UiException(ex.getMessage(),ex);
			}
		}else{
			String orgMsg = ex.getMessage();
			String msg = formatLocationMessage(null,location,showColumn);
			if(orgMsg.endsWith(msg)){
				//don't append if the location info is the same.
				if(ex instanceof RuntimeException){
					return (RuntimeException) ex;
				}else{
					return new UiException(ex.getMessage(),ex);
				}
			}else{
				msg = formatLocationMessage(orgMsg,location,showColumn);
				//no way to change exception's message, so use the most common UiException
				return new UiException(msg,ex);
			}
			
		}
	}
}
