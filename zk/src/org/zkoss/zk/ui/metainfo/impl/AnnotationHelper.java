/* AnnotationHelper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug  6 15:48:07     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo.impl;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import org.zkoss.lang.Strings;
import org.zkoss.util.Maps;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * A helper class used to parse annotations.
 *
 * <p>How to use:
 * <ol>
 * <li>Invoke one of {@link #add}, {@link #addByRawValue},
 * or {@link #addByCompoundValue} to add annotations to this helper.</li>
 * <li>After annotations are all added, invoke {@link #applyAnnotations}
 * to update the annotations to the specified component info.</li>
 * </ol>
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class AnnotationHelper {
	/** A list of Object[] = {String annotName, Map annotAttrs}; */
	final List _annots = new LinkedList();

	/** Adds an annotation definition.
	 * The annotation's attributes must be parsed into a map (annotAttrs).
	 *
	 * @param annotName the annotation name.
	 * @param annotAttrs a map of attributes of the annotation. If null,
	 * it means no attribute at all.
	 * @see #addByRawValue
	 * @see #addByCompoundValue
	 */
	public void add(String annotName, Map annotAttrs) {
		if (annotName == null || annotName.length() == 0)
			throw new IllegalArgumentException("empty");
		_annots.add(new Object[] {annotName, annotAttrs});
	}
	/** Adds an annotation by specify the value in the raw format:
	 * <code>att1-name=att1-value, att2-name = att2-value</code>.
	 */
	public void addByRawValue(String annotName, String rawValue) {
		final Map attrs = Maps.parse(null, rawValue, ',', '\'', true);
		add(annotName, attrs);
	}
	/** Adds annotation by specifying the content in the compound format:
	 * <code>annot-name(att1-name=att1-value, att2-name=att2-value)</code>.
	 */
	public void addByCompoundValue(String cval) {
		final char[] seps1 = {'(', ' '}, seps2 = {')'};
		for (int j = 0, len = cval.length(); j < len;) {
			j = Strings.skipWhitespaces(cval, j);
			int k = Strings.nextSeparator(cval, j, seps1, true, true, false);
			if (k < len && cval.charAt(k) == '(') {
				String nm = cval.substring(j, k).trim();
				if (nm.length() == 0) nm = "default";

				j = k + 1;
				k = Strings.nextSeparator(cval, j, seps2, true, true, false);

				final String rv = 
					(k < len ? cval.substring(j, k): cval.substring(j)).trim();
				if (rv.length() > 0)
					addByRawValue(nm, rv);
				else
					add(nm, null);
			} else {
				final String rv = 
					(k < len ? cval.substring(j, k): cval.substring(j)).trim();
				if (rv.length() > 0)
					addByRawValue("default", rv);
			}
			j = k + 1;
		}
	}

	/** Applies the annotations defined in this helper to the specified
	 * instance definition.
	 *
	 * @param compInfo the instance definition to update
	 * @param propName the property name
	 * @param clear whether to clear all definitions before returning
	 * @see #clear
	 */
	public void applyAnnotations(ComponentInfo compInfo, String propName,
	boolean clear) {
		for (Iterator it = _annots.iterator(); it.hasNext();) {
			final Object[] info = (Object[])it.next();
			final String annotName = (String)info[0];
			final Map annotAttrs = (Map)info[1];
			if (propName != null)
				compInfo.addAnnotation(propName, annotName, annotAttrs);
			else
				compInfo.addAnnotation(annotName, annotAttrs);
		}
		if (clear)
			_annots.clear();
	}
	/** Applies the annotations defined in this helper to the specified
	 * component.
	 *
	 * @param comp the component to update
	 * @param propName the property name
	 * @param clear whether to clear all definitions before returning
	 * @see #clear
	 */
	public void applyAnnotations(Component comp, String propName,
	boolean clear) {
		for (Iterator it = _annots.iterator(); it.hasNext();) {
			final Object[] info = (Object[])it.next();
			final String annotName = (String)info[0];
			final Map annotAttrs = (Map)info[1];
			ComponentCtrl ctrl = (ComponentCtrl) comp;
			if (propName != null)
				ctrl.addAnnotation(propName, annotName, annotAttrs);
			else
				ctrl.addAnnotation(annotName, annotAttrs);
		}
		if (clear)
			_annots.clear();
	}
	/** Clears the annotations defined in this helper.
	 *
	 * <p>The annotations are defined by {@link #add}, {@link #addByRawValue},
	 * or {@link #addByCompoundValue}.
	 *
	 * @return true if one or more annotation definitions are defined
	 * (thru {@link #add}).
	 */
	public boolean clear() {
		if (!_annots.isEmpty()) {
			_annots.clear();
			return true;
		}
		return false;
	}
}
