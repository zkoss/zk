/* AnnotationMapImpl.java

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
package org.zkoss.zk.ui.metainfo.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.zkoss.zk.ui.metainfo.*;

/**
 * An implementation of {@link AnnotationMap}.
 *
 * <p>Note: it is not thread-safe.
 *
 * @author tomyeh
 */
public class AnnotationMapImpl implements AnnotationMap, java.io.Serializable {
	/** The annotations of properties, (String propName, Map(String annotName, Annotation)).
	 */
	private Map _annots;

	//--AnnotationMap--//
	public Annotation getAnnotation(String annotName) {
		return getAnnotation0(null, annotName);
	}
	public Annotation getAnnotation(String propName, String annotName) {
		if (propName == null || propName.length() == 0)
			throw new IllegalArgumentException("The property name is required");
		return getAnnotation0(propName, annotName);
	}
	public Collection getAnnotations() {
		return getAnnotations0(null);
	}
	public Collection getAnnotations(String propName) {
		if (propName == null || propName.length() == 0)
			throw new IllegalArgumentException("The property name is required");
		return getAnnotations0(propName);
	}
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

	//--extra (write)--//
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

	private void addAnnotation0(String propName, String annotName, Map annotAttrs) {
		if (_annots == null)
			_annots = new HashMap(3);

		Map ans = (Map)_annots.get(propName);
		if (ans == null)
			_annots.put(propName, ans = new LinkedHashMap(3));

		AnnotImpl ai = (AnnotImpl)ans.get(annotName);
		if (ai == null)
			ans.put(annotName, ai = new AnnotImpl(annotName));

		ai.addAttributes(annotAttrs);
	}

	//Cloneable//
	public Object clone() {
		final AnnotationMapImpl clone;
		try {
			clone = (AnnotationMapImpl)super.clone();
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

	/** An implementation of {@link Annotation}. */
	private class AnnotImpl implements Annotation {
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
				_attrs = new LinkedHashMap(5);
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
			return _attrs != null ? Collections.unmodifiableMap(_attrs):
				Collections.EMPTY_MAP;
		}
		public String getAttribute(String name) {
			return _attrs != null ? (String)_attrs.get(name): null;
		}
		public String toString() {
			return '[' + _name + ": " + _attrs + ']';
		}
	}
}
