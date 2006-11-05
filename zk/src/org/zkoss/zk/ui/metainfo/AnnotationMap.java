/* AnnotationMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Nov  4 10:39:08     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A map of annotations used with {@link InstanceDefinition}.
 *
 * @author tomyeh
 */
public class AnnotationMap implements java.io.Serializable, Cloneable {
	/** The annotations of properties, (String propName, Map(String annotName, Annotation)).
	 */
	private Map _annots;

	/** Returns the annotation associated with the component definition,
	 * or null if not available.
	 *
	 * @param annotName the annotation name
	 */
	public Annotation getAnnotation(String annotName) {
		return getAnnotation0(null, annotName);
	}
	/** Returns the annotation associated with the definition of the specified
	 * property, or null if not available.
	 *
	 * @param annotName the annotation name
	 * @param propName the property name, e.g., "value".
	 * @exception IllegalArgumentException if propName is null or empty
	 */
	public Annotation getAnnotation(String propName, String annotName) {
		if (propName == null || propName.length() == 0)
			throw new IllegalArgumentException("The property name is required");
		return getAnnotation0(propName, annotName);
	}
	/** Returns a read-only collection of all annotations associated with the
	 * component definition (never null).
	 */
	public Collection getAnnotations() {
		return getAnnotations0(null);
	}
	/** Returns a read-only collection of all annotations associated with the
	 * definition of the specified property (never null).
	 *
	 * @param propName the property name, e.g., "value".
	 * @exception IllegalArgumentException if propName is null or empty
	 */
	public Collection getAnnotations(String propName) {
		if (propName == null || propName.length() == 0)
			throw new IllegalArgumentException("The property name is required");
		return getAnnotations0(propName);
	}
	/** Returns a read-only list of the names (String) of the properties
	 * that are associated with the specified annotation (never null).
	 */
	public List getAnnotatedPropertiesBy(String annotName) {
		List list = null;
		if (_annots != null) {
			for (Iterator it = _annots.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String propName = (String)me.getKey();
				if (propName != null) {
					final Map ans = (Map)me.getValue();
					if (ans.containsKey(annotName)) {
						if (list == null) list = new LinkedList();
						list.add(propName);
					}
				}
			}
		}
		return list != null ? list: Collections.EMPTY_LIST;
	}
	/** Returns a read-only list of the name (String) of properties that
	 * are associated at least one annotation (never null).
	 */
	public List getAnnotatedProperties() {
		List list = null;
		if (_annots != null) {
			for (Iterator it = _annots.keySet().iterator(); it.hasNext();) {
				final String propName = (String)it.next();
				if (propName != null) {
					if (list == null) list = new LinkedList();
					list.add(propName);
				}
			}
		}
		return list != null ? list: Collections.EMPTY_LIST;
	}
	/** Adds an annotation.
	 */
	public void addAnnotation(Annotation annot) {
		addAnnotation0(null, annot);
	}
	/** Adds an annotation to a proeprty.
	 *
	 * @param propName the property name.
	 */
	public void addAnnotation(String propName, Annotation annot) {
		if (propName == null || propName.length() == 0)
			throw new IllegalArgumentException("The property name is required");
		addAnnotation0(propName, annot);
	}

	private Annotation getAnnotation0(String propName, String annotName) {
		if (_annots != null) {
			final Map ans = (Map)_annots.get(propName);
			if (ans != null) return (Annotation)ans.get(annotName);
		}
		return null;
	}
	private Collection getAnnotations0(String propName) {
		if (_annots != null) {
			final Map ans = (Map)_annots.get(propName);
			if (ans != null) return ans.values();
		}
		return Collections.EMPTY_LIST;
	}

	private void addAnnotation0(String propnm, Annotation annot) {
		if (annot == null)
			throw new IllegalArgumentException("null annot");
		if (_annots == null)
			_annots = new HashMap(3);

		Map ans = (Map)_annots.get(propnm);
		if (ans == null)
			_annots.put(propnm, ans = new LinkedHashMap(3));

		ans.put(annot.getName(), annot);
	}

	//Cloneable//
	public Object clone() {
		final AnnotationMap clone;
		try {
			clone = (AnnotationMap)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		if (clone._annots != null) {
			clone._annots = new HashMap(clone._annots);
			for (Iterator it = clone._annots.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				me.setValue(new LinkedHashMap((HashMap)me.getValue()));
			}
		}
		return clone;
	}
}
