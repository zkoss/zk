/* AnnotationUtil.java

	Purpose:
		
	Description:
		
	History:
		2012/2/24 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * A internal util to help processing component annotation, for internal using only.
 * 
 * @author dennis
 * @since 6.0.1
 */
public class AnnotationUtil {
	
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
	
	public static String testString(String[] string, Component comp,String propName, String tag){
		if(string==null || string.length==0){
			return null;
		}else if(string.length==1){
			return string[0];
		}else{
			throw new IllegalSyntaxException("only allow one string of "+tag +" attribute="+propName+" on comp= "+comp+", but contains "+Arrays.toString(string));
		}
	}
}
