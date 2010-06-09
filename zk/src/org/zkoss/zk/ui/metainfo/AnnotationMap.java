/* AnnotationMap.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec  4 16:09:53     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
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
 * A map of annotations used with {@link ComponentDefinition} and
 * {@link ComponentInfo}.
 *
 * <p>Note: it is not thread safe.
 *
 * @author tomyeh
 */
public class AnnotationMap implements Cloneable, java.io.Serializable {
	/** The annotations of properties,
	 * (String propName, Map(String annotName, AnnotImpl)).
	 */
	private Map _annots;

	/** Returns whether no annotation at all.
	 */
	public boolean isEmpty() {
		return _annots == null || _annots.isEmpty();
	}
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
				final Object propName = me.getKey();
				if (propName != null) {
					final Map ans = (Map)me.getValue(); //ans is syncMap
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
				final Object propName = it.next();
				if (propName != null) {
					if (list == null) list = new LinkedList();
					list.add(propName);
				}
			}
		}
		return list != null ? list: Collections.EMPTY_LIST;
	}

	//Modification API//
	/** Adds all annotations of the specified map to this map.
	 */
	public void addAll(AnnotationMap src) {
		if (src != null && !src.isEmpty()) {
			initAnnots();

			for (Iterator it = src._annots.entrySet().iterator();
			it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final Object propName = me.getKey(); //may be null

				Map ans = (Map)_annots.get(propName);
				if (ans == null)
					_annots.put(propName, ans = newAnnotImpls());

				addAllAns(ans, (Map)me.getValue());
			}
		}			
	}
	/** Adds the value of _annots, Map(String annotName, AnnotImpl).
	 */
	public static void addAllAns(Map ans, Map srcans) {
		for (Iterator it = srcans.entrySet().iterator();
		it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String annotName = (String)me.getKey();

			AnnotImpl ai = (AnnotImpl)ans.get(annotName);
			if (ai == null)
				ans.put(annotName, ai = new AnnotImpl(annotName));

			ai.addAttributes(((AnnotImpl)me.getValue())._attrs);
		}
	}
	/** Adds an annotation.
	 */
	public void addAnnotation(String annotName, Map annotAttrs) {
		addAnnotation0(null, annotName, annotAttrs);
	}
	/** Adds an annotation to a proeprty.
	 *
	 * @param propName the property name.
	 */
	public void addAnnotation(String propName, String annotName, Map annotAttrs) {
		if (propName == null || propName.length() == 0)
			throw new IllegalArgumentException("The property name is required");
		addAnnotation0(propName, annotName, annotAttrs);
	}

	private Annotation getAnnotation0(String propName, String annotName) {
		if (_annots != null) {
			final Map ans = (Map)_annots.get(propName);
			if (ans != null)
				return (Annotation)ans.get(annotName); //ans is syncMap
		}
		return null;
	}
	private Collection getAnnotations0(String propName) {
		if (_annots != null) {
			final Map ans = (Map)_annots.get(propName);
			if (ans != null)
				return ans.values(); //ans is syncMap
		}
		return Collections.EMPTY_LIST;
	}

	private void addAnnotation0(String propName, String annotName, Map annotAttrs) {
		initAnnots();

		Map ans = (Map)_annots.get(propName);
		if (ans == null)
			_annots.put(propName, ans = newAnnotImpls());

		AnnotImpl ai = (AnnotImpl)ans.get(annotName);
		if (ai == null)
			ans.put(annotName, ai = new AnnotImpl(annotName));

		ai.addAttributes(annotAttrs);
	}
	/** Initializes _annots by creating and assigning a new map for it.
	 */
	private void initAnnots() {
		if (_annots == null)
			_annots = new HashMap(4);
	}
	/** Create a map used for (String name, AnnotImpl annot).
	 */
	private static Map newAnnotImpls() {
		return new LinkedHashMap(4);
	}

	//Cloneable//
	/** Clones this annotation map.
	 */
	public Object clone() {
		final AnnotationMap clone;
		try {
			clone = (AnnotationMap)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}

		if (_annots != null) {
			clone._annots = new HashMap(_annots);

			for (Iterator it = clone._annots.entrySet().iterator();
			it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final Map ans = clone.newAnnotImpls();
				clone.addAllAns(ans, (Map)me.getValue());
				me.setValue(ans); //replace with the new one
			}
		}
		return clone;
	}
	public String toString() {
		return "[annot:" + _annots + ']';
	}

	/** An implementation of {@link Annotation}.
	 */
	private static class AnnotImpl implements Annotation {
		private final String _name;
		private Map _attrs;

		private AnnotImpl(String name) {
			_name = name;
		}

		//Extra//
		/** Adds the specified attribute to the annotation.
		 *
		 * @param name the attribute name. "value" is assumed if name is null or empty.
		 * @param value the attribute value. If null, "" is assumed (not removal).
		 */
		private void addAttribute(String name, String value) {
			if (name == null || name.length() == 0)
				name = "value";
			if (value == null)
				value = "";

			if (_attrs == null)
				_attrs = new LinkedHashMap(3);
			_attrs.put(name, value);
		}
		/** Adds a map of attributes, (String name, String value), to the annotation.
		 */
		private void addAttributes(Map attrs) {
			if (attrs != null) {
				for (Iterator it = attrs.entrySet().iterator(); it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					addAttribute((String)me.getKey(), (String)me.getValue());
				}
			}
		}

		//Annotation//
		public String getName() {
			return _name;
		}
		public Map getAttributes() {
			return _attrs != null ? _attrs: Collections.EMPTY_MAP;
		}
		public String getAttribute(String name) {
			return _attrs != null ? (String)_attrs.get(name): null;
		}
		public String toString() {
			return '[' + _name + ": " + _attrs + ']';
		}
	}
}
