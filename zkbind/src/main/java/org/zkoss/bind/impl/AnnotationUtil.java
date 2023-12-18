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
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * An internal utility to help processing component annotation, for internal using only.
 * 
 * @author dennis
 * @since 6.0.1
 */
public class AnnotationUtil {

	// Deal with multiple annotations
	private static Annotation getActivatedAnnotation(Collection<Annotation> annos) {
		// Use last defined annotation instead of default annotation if any
		if (annos instanceof List) {
			return ((List<Annotation>) annos).get(annos.size() - 1);
		}
		Iterator<Annotation> it = annos.iterator();
		Annotation anno = it.next();
		while (it.hasNext()) {
			anno = it.next();
		}
		return anno;
	}

	/**
	 * @since 6.5.4
	 */
	//can't use ':' to compatible with @ComponentAnnotation
	public static final String ZKBIND_PREFIX = Binder.ZKBIND + "$";

	//ZK-1908 Databinding Load order causing problems on Paging component.
	//System annotation shouldn't effect the annotation sequence
	/**
	 * @since 6.5.4
	 */
	@SuppressWarnings("rawtypes")
	public static Annotation getSystemAnnotation(ComponentCtrl compCtrl, String propName) {
		//compatible to old spec, gets no ZKBIND prefix in annotation property first
		Collection<Annotation> annos = compCtrl.getAnnotations(propName, Binder.ZKBIND);
		if (annos.isEmpty()) {
			if (propName == null)
				return null;
			annos = compCtrl.getAnnotations(ZKBIND_PREFIX + propName, Binder.ZKBIND);
			if (annos.isEmpty())
				return null;
		}

		return getActivatedAnnotation(annos);
	}

	//ZK-1908 Databinding Load order causing problems on Paging component.
	public static List<String> getNonSystemProperties(Component comp) {
		final ComponentCtrl compCtrl = (ComponentCtrl) comp;
		List<String> props = compCtrl.getAnnotatedProperties();

		if (props == null || props.size() == 0)
			return Collections.emptyList();

		List<String> propsList = new ArrayList<String>(props.size());
		for (String p : props) {
			if (p.startsWith(ZKBIND_PREFIX))
				continue;
			propsList.add(p);
		}
		return propsList;
	}

	public static String testString(String[] string, Annotation anno) {
		if (string == null || string.length == 0) {
			return null;
		} else if (string.length == 1) {
			return string[0];
		} else {
			throw new IllegalSyntaxException(MiscUtil.formatLocationMessage(
					"only allow one string of @" + anno.getName() + ",but contains " + Arrays.toString(string), anno));
		}
	}
}
