/* TaglibMapper.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 12:43:56     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;

import org.zkoss.lang.Objects;
import org.zkoss.util.resource.Locator;
import org.zkoss.idom.Element;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.Function;
import org.zkoss.xel.XelException;
import org.zkoss.xel.taglib.Taglibs;
import org.zkoss.xel.taglib.Taglib;

/**
 * A function mapper that is capable to load function and class definitions
 * from taglib.
 *
 * @author tomyeh
 * @since 3.0.0
 * @since Taglib
 * @since Taglibs
 */
public class TaglibMapper implements FunctionMapper, Cloneable, java.io.Serializable {
	/** Map(String prefix+":"+name, Function func). */
	protected Map<String, Function> _mtds;
	/** Map(String name, Class cls). */
	protected Map<String, Class> _clses;

	public TaglibMapper() {
	}

	/** Adds the class that can be retrieved by {@link #resolveClass}.
	 *
	 * @param name the logic name of the class
	 * @param cls the class to import
	 */
	public void addClass(String name, Class cls) {
		if (name == null || name.length() == 0 || cls == null)
			throw new IllegalArgumentException();
		if (_clses == null)
			_clses = new HashMap<String, Class>(4);
		_clses.put(name, cls);
	}
	/** Adds the function that can be retrieved by {@link #resolveFunction}.
	 *
	 * @param prefix the prefix of the name
	 * @param name the logic name of the function
	 * @param func the function
	 */
	public void addFunction(String prefix, String name, Function func) {
		if (name == null || name.length() == 0 || func == null)
			throw new IllegalArgumentException();
		if (_mtds == null)
			_mtds = new HashMap<String, Function>(4);
		_mtds.put(prefix + ":" + name, func);
	}

	/** Loads function and class definitions from taglib.
	 */
	public void load(String prefix, URL url) throws XelException {
		try {
			load0(prefix, Taglibs.load(url)); //load from cache
		} catch (Exception ex) {
			throw XelException.Aide.wrap(ex);
		}
	}
	/** Loads function and class definitions from taglib.
	 */
	public void load(Taglib taglib, Locator locator) {
		load(taglib.getPrefix(), taglib.getURI(), locator);
	}
	/** Loads function and class definitions from taglib.
	 */
	public void load(String prefix, String uri, Locator locator)
	throws XelException {
		if (prefix == null || uri == null)
			throw new IllegalArgumentException("null");
		if (_mtds != null && _mtds.containsKey(prefix))
			throw new XelException("The prefix, "+prefix+", is already used");

		URL url = uri.indexOf("://") > 0 ? null: locator.getResource(uri);
		if (url == null) {
			url = Taglibs.getDefaultURL(uri);
			if (url == null)
				throw new XelException("Resource not found: "+uri);
		}

		load(prefix, url);
	}
	/** Loads function and class definitions from DOM.
	 */
	public void load(String prefix, Element root)
	throws XelException {
		if (prefix == null || root == null)
			throw new IllegalArgumentException("null");
		if (_mtds != null && _mtds.containsKey(prefix))
			throw new XelException("The prefix, "+prefix+", is already used");

		try {
			load0(prefix, Taglibs.load(root));
		} catch (Exception ex) {
			throw XelException.Aide.wrap(ex);
		}
	}
	@SuppressWarnings("unchecked")
	private void load0(String prefix, Map[] loaded) {
		if (!loaded[0].isEmpty()) {
			if (_mtds == null)
				_mtds = new HashMap<String, Function>(8);
			for (Iterator e = loaded[0].entrySet().iterator(); e.hasNext();) {
				final Map.Entry me = (Map.Entry)e.next();
				addFunction(prefix, (String)me.getKey(), (Function)me.getValue());
			}
		}

		if (!loaded[1].isEmpty()) {
			if (_clses == null)
				_clses = new HashMap<String, Class>(4);
			_clses.putAll(loaded[1]);
		}
	}

	//-- FunctionMapper --//
	public Function resolveFunction(String prefix, String name) {
		return _mtds != null ? _mtds.get(prefix+":"+name): null;
	}
	public Collection<String> getClassNames() {
		if (_clses != null)
			return _clses.keySet();
		return Collections.emptyList();
	}
	public Class resolveClass(String name) {
		return _clses != null ? (Class)_clses.get(name): null;
	}

	//-- Cloneable --//
	public Object clone() {
		final TaglibMapper clone;
		try {
			clone = (TaglibMapper)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}

		if (_mtds != null)
			clone._mtds = new HashMap<String, Function>(clone._mtds);
		if (_clses != null)
			clone._clses = new HashMap<String, Class>(clone._clses);
		return clone;
	}

	//Object//
	public int hashCode() {
		return Objects.hashCode(_mtds) ^ Objects.hashCode(_clses);
	}
	public boolean equals(Object o) {
		return o instanceof TaglibMapper
			&& Objects.equals(_mtds, ((TaglibMapper)o)._mtds)
			&& Objects.equals(_clses, ((TaglibMapper)o)._clses);
	}
}
