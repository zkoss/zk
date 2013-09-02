/* AnnotationUtil.java

	Purpose:
		
	Description:
		
	History:
		2012/2/24 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.zkoss.bind.Binder;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.annotation.ComponentAnnotation;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * An internal utility to help processing component annotation, for internal using only.
 * 
 * @author dennis
 * @since 6.0.1
 */
public class AnnotationUtil {
	
	/**
	 * @deprecated since 6.5.4
	 */
	@SuppressWarnings("rawtypes")
	public static Annotation getOverrideAnnotation(ComponentCtrl compCtrl, String propName, String annoName){
		Collection<Annotation> annos = compCtrl.getAnnotations(propName, annoName);
		if(annos.size()<=0) return null;
		//TODO the real implementation, currently I use the last one
		if(annos instanceof List){
			return (Annotation)((List)annos).get(((List)annos).size()-1);
		}
		Iterator<Annotation> it = annos.iterator();
		Annotation anno = it.next();
		while(it.hasNext()){
			anno = it.next();
		}
		return anno;
	}
	
	/**
	 * @since 6.5.4
	 */
	//can't use ':' to compatible with @ComponentAnnotation
	public static final String ZKBIND_REFIX = Binder.ZKBIND+"$";
	//ZK-1908 Databinding Load order causing problems on Paging component.
	//System annotation shouldn't effect the annotation sequence
	/**
	 * @since 6.5.4
	 */
	@SuppressWarnings("rawtypes")
	public static Annotation getSystemAnnotation(ComponentCtrl compCtrl, String propName){
		//compatible to old spec, gets no ZKBIND prefix in annotation property first
		Collection<Annotation> annos = compCtrl.getAnnotations(propName, Binder.ZKBIND);
		if(annos.size()<=0){
			if(propName==null) 
				return null;
			annos = compCtrl.getAnnotations(ZKBIND_REFIX+propName, Binder.ZKBIND);
			if(annos.size()<=0) return null;
		}
		//TODO decide which one should use, currently I use the last one
		if(annos instanceof List){
			return (Annotation)((List)annos).get(((List)annos).size()-1);
		}
		Iterator<Annotation> it = annos.iterator();
		Annotation anno = it.next();
		while(it.hasNext()){
			anno = it.next();
		}
		return anno;
	}
	
	//ZK-1908 Databinding Load order causing problems on Paging component.
	//introduce(6.5.4) a new spec of annotation on attribute, now allow to add a priority(large number is higher,default 0)
	//after property name('property:priority')
	public static List<String> getNonSystemProperties(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		List<String> props = compCtrl.getAnnotatedProperties();
		
		if(props==null || props.size()==0)
			return Collections.EMPTY_LIST;
		//'prop:priority' -> object[]{'prop',priority}
		List<String> propsList = new ArrayList<String>(props.size());
		for(String p:props){//
			if(p.startsWith(ZKBIND_REFIX))
				continue;
			propsList.add(p);	
		}
		return propsList;
	}
	
//	public static String testString(String[] string, Component comp,String propName, String tag){
//		return testString(string, comp, propName, tag);
//	}
	public static String testString(String[] string,Annotation anno){
		if(string==null || string.length==0){
			return null;
		}else if(string.length==1){
			return string[0];
		}else{
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage(
					"only allow one string of @" + anno.getName() + ",but contains " + Arrays.toString(string),anno));
		}
	}
}
