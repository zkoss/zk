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
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.zkoss.lang.Objects;
import static org.zkoss.lang.Generics.cast;
import org.zkoss.util.ArraysX;
import org.zkoss.util.resource.Location;
import org.zkoss.zk.ui.UiException;

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
	 * (String propName, Map(String annotName, Annotation)).
	 */
	private Map<String, Map<String, List<Annotation>>> _annots;

	/** Returns whether no annotation at all.
	 */
	public boolean isEmpty() {
		return _annots == null || _annots.isEmpty();
	}

	/** Returns the annotation associated with the specified
	 * property, or null if not available.
	 *
	 * @param annotName the annotation name
	 * @param propName the property name, e.g., "value".
	 * If null, this method returns the annotation(s) associated with the
	 * component (rather than a particular property).
	 */
	public Annotation getAnnotation(String propName, String annotName) {
		if (_annots != null) {
			final Map<String, List<Annotation>> anmap = _annots.get(propName);
			if (anmap != null) {
				List<Annotation> ans = anmap.get(annotName);
				if (ans != null) {
					if (ans.size() == 1)
						return ans.get(0);

					//merge annotations into a single annotation
					final AnnotImpl ai = new AnnotImpl(annotName, null);
					for (Annotation an: ans) {
						if (ai._loc == null)
							ai._loc = an.getLocation();
						ai.addAttributes(an);
					}
					return ai;
				}
			}
		}
		return null;
	}
	/** Returns the annotations associated with the specified
	 * property. It never returns null.
	 *
	 * @param annotName the annotation name
	 * @param propName the property name, e.g., "value".
	 * If null, this method returns the annotation(s) associated with the
	 * component (rather than a particular property).
	 * @since 6.0.0
	 */
	public Collection<Annotation> getAnnotations(String propName, String annotName) {
		if (_annots != null) {
			final Map<String, List<Annotation>> anmap = _annots.get(propName);
			if (anmap != null) {
				List<Annotation> ans = anmap.get(annotName);
				if (ans != null)
					return ans;
			}
		}
		return Collections.emptyList();
	}
	/** Returns a read-only collection of all annotations associated with the
	 * the specified property.
	 *
	 * @param propName the property name, e.g., "value".
	 * If null, this method returns the annotation(s) associated with the
	 * component (rather than a particular property).
	 */
	public Collection<Annotation> getAnnotations(String propName) {
		if (_annots != null) {
			final Map<String, List<Annotation>> anmap = _annots.get(propName);
			if (anmap != null) {
				final List<Annotation> dst = new LinkedList<Annotation>();
				for (List<Annotation> ans: anmap.values())
					dst.addAll(ans);
				return dst;
			}
		}
		return Collections.emptyList();
	}
	/** Returns a read-only list of the names (String) of the properties
	 * that are associated with the specified annotation (never null).
	 */
	public List<String> getAnnotatedPropertiesBy(String annotName) {
		if (_annots != null) {
			final List<String> list = new LinkedList<String>();
			for (Map.Entry<String, Map<String, List<Annotation>>> me: _annots.entrySet()) {
				final String propName = me.getKey();
				if (propName != null) {
					final Map<String, List<Annotation>> anmap = me.getValue();
					if (anmap.containsKey(annotName))
						list.add(propName);
				}
			}
			return list;
		}
		return Collections.emptyList();
	}
	/** Returns a read-only list of the name (String) of properties that
	 * are associated at least one annotation (never null).
	 */
	public List<String> getAnnotatedProperties() {
		final List<String> list = new LinkedList<String>();
		if (_annots != null) {
			for (String propName: _annots.keySet()) {
				if (propName != null)
					list.add(propName);
			}
		}
		return list;
	}

	//Modification API//
	/** Adds all annotations of the specified map to this map.
	 */
	public void addAll(AnnotationMap src) {
		if (src != null && !src.isEmpty()) {
			initAnnots();

			for (Map.Entry<String, Map<String, List<Annotation>>> me:
			src._annots.entrySet()) {
				final String propName = me.getKey(); //may be null
				Map<String, List<Annotation>> anmap = _annots.get(propName);
				if (anmap == null)
					_annots.put(propName, anmap = new LinkedHashMap<String, List<Annotation>>(4));

				addAllAns(anmap, me.getValue());
			}
		}			
	}
	/** Adds the annotations from one source to another.
	 * @param anmap the destination
	 * @param srcanmap the source
	 */
	private static
	void addAllAns(Map<String, List<Annotation>> anmap, Map<String, List<Annotation>> srcanmap) {
		for (Map.Entry<String, List<Annotation>> me: srcanmap.entrySet()) {
			final String annotName = me.getKey();
			List<Annotation> ans = anmap.get(annotName);
			if (ans == null)
				anmap.put(annotName, ans = new LinkedList<Annotation>());
			ans.addAll(me.getValue());
		}
	}

	/** Adds an annotation.
	 *
	 * @param propName the property name.
	 * If null, this method returns the annotation(s) associated with the
	 * component (rather than a particular property).
	 * @since 6.0.0
	 */
	public void addAnnotation(String propName, String annotName,
	Map<String, String[]> annotAttrs, Location loc) {
		initAnnots();

		Map<String, List<Annotation>> anmap = _annots.get(propName);
		if (anmap == null)
			_annots.put(propName, anmap = new LinkedHashMap<String, List<Annotation>>(4));

		List<Annotation> ans = anmap.get(annotName);
		if (ans == null)
			anmap.put(annotName, ans= new LinkedList<Annotation>());

		ans.add(new AnnotImpl(annotName, fixAttrValues(annotAttrs), loc));
	}
	/** Used to resolve the backward compatibility:
	 * ZK 6 expects String[], but ZK 5 might pass String as the value.
	 */
	private Map<String, String[]> fixAttrValues(Map<?, ?> attrs) {
		if (attrs == null || attrs.isEmpty())
			return null;

		for (Map.Entry<?, ?> m0: attrs.entrySet()) {
			Object key = m0.getKey();
			Object val = m0.getValue();
			if ((key != null && !(key instanceof String))
			|| !(val instanceof String[])) {//need to convert
				final Map<String, String[]> as = new LinkedHashMap<String, String[]>(4);
				for (Map.Entry<?, ?> me: attrs.entrySet()) {
					key = me.getKey();
					if (key != null  && !(key instanceof String))
						throw new UiException("Illegal attribute name, "+key);

					val = me.getValue();
					if (val == null || val instanceof String[])
						as.put((String)key, (String[])val);
					else if (val instanceof String)
						as.put((String)key, new String[] {(String)val});
					else
						throw new UiException("Illegagl attribute value, "+val);
				}
				return as;
			}
		}
		return cast(attrs);
	}
	/** Initializes _annots by creating and assigning a new map for it.
	 */
	private void initAnnots() {
		if (_annots == null)
			_annots = new LinkedHashMap<String, Map<String, List<Annotation>>>(4);
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
			clone._annots = new LinkedHashMap<String, Map<String, List<Annotation>>>(_annots);

			for (Map.Entry<String, Map<String, List<Annotation>>> me:
			clone._annots.entrySet()) {
				final Map<String, List<Annotation>> anmap = new LinkedHashMap<String, List<Annotation>>(4);
				clone.addAllAns(anmap, me.getValue());
				me.setValue(anmap); //replace with the new one
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
		private Map<String, String[]> _attrs;
		private Location _loc;

		private AnnotImpl(String name, Location loc) {
			_name = name;
			_loc = loc;
		}
		private AnnotImpl(String name, Map<String, String[]> attrs, Location loc) {
			_name = name;
			_loc = loc;
			addAttributes(attrs);
		}

		//Extra//
		/** Adds the specified attribute to the annotation.
		 *
		 * @param name the attribute name. "value" is assumed if name is null or empty.
		 * @param value the attribute value. If null, "" is assumed (not removal).
		 */
		private void addAttribute(String name, String[] value) {
			if (name == null || name.length() == 0)
				name = "value";
			if (value == null)
				value = new String[0];

			if (_attrs == null) {
				_attrs = new LinkedHashMap<String, String[]>(4);
			} else {
				final String[] oldval = _attrs.get(name);
				if (oldval != null)
					value = (String[])ArraysX.concat(oldval, value);
			}

			_attrs.put(name, value);
		}
		/** Adds a map of attributes, (String name, String value), to the annotation.
		 */
		private void addAttributes(Map<String, String[]> attrs) {
			if (attrs != null)
				for (Map.Entry<String, String[]> me: attrs.entrySet())
					addAttribute(me.getKey(), me.getValue());
		}
		/** Adds the attributes of the given annotation to this annotation.
		 */
		private void addAttributes(Annotation an) {
			if (an != null)
				addAttributes(((AnnotImpl)an)._attrs);
		}

		@Override
		public String getName() {
			return _name;
		}
		@Override
		public Map<String, String[]> getAttributes() {
			if (_attrs != null)
				return _attrs;
			return Collections.emptyMap();
		}
		@Override
		public String getAttribute(String name) {
			if (_attrs != null) {
				String[] val = _attrs.get(name);
				return val != null && val.length > 0 ? val[0]: null;
			}
			return null;
		}
		@Override
		public String[] getAttributeValues(String name) {
			return _attrs != null ? _attrs.get(name): null;
		}
		@Override
		public Location getLocation() {
			return _loc;
		}
		@Override
		public String toString() {
			final StringBuffer sb =
				new StringBuffer().append('@').append(_name).append('(');
			if (_attrs != null) {
				boolean first = true;
				for (Map.Entry<String, String[]> me: _attrs.entrySet()) {
					if (first) first = false;
					else sb.append(", ");

					sb.append(me.getKey()).append('=')
						.append(Objects.toString(me.getValue()));
				}
			}
			return sb.append(')').toString();
		}
	}
}
