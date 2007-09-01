/* SimpleMapper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 12:43:56     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

import java.util.Map;
import java.util.HashMap;
import java.net.URL;

import org.zkoss.util.resource.Locator;
import org.zkoss.idom.Element;

/**
 * A simple function mapper.
 *
 * @author tomyeh
 */
public class SimpleMapper implements FunctionMapper {
	private final FunctionMapper _parent;
	/** Map(String prefix, Map(String, Function)). */
	private Map _maps;

	public SimpleMapper() {
		this(null);
	}
	public SimpleMapper(FunctionMapper parent) {
		if (parent == this)
			throw new IllegalArgumentException("parent cannot be itself");
		_parent = parent;
	}

	/** Loads function definitions from taglib. */
	public void load(String prefix, String uri, Locator locator)
	throws XelException {
		if (prefix == null || uri == null)
			throw new IllegalArgumentException("null");
		if (_maps != null && _maps.containsKey(prefix))
			throw new XelException("The prefix, "+prefix+", is already used");

		final URL url = locator.getResource(uri);
		if (url == null)
			throw new XelException("Resource not found: "+uri);

		if (_maps == null)
			_maps = new HashMap(4);
		try {
			_maps.put(prefix, FunctionMappers.loadFunctions(url));
		} catch (Exception ex) {
			throw XelException.Aide.wrap(ex);
		}
	}
	/** Loads function definitions from DOM. */
	public void load(String prefix, Element root)
	throws XelException {
		if (prefix == null || root == null)
			throw new IllegalArgumentException("null");
		if (_maps != null && _maps.containsKey(prefix))
			throw new XelException("The prefix, "+prefix+", is already used");

		if (_maps == null)
			_maps = new HashMap(4);
		try {
			_maps.put(prefix, FunctionMappers.loadFunctions(root));
		} catch (Exception ex) {
			throw XelException.Aide.wrap(ex);
		}
	}

	//-- FunctionMapper --//
	public Function resolveFunction(String prefix, String name) {
		final Map mtds = _maps != null ? (Map)_maps.get(prefix): null;
		return mtds != null ? (Function)mtds.get(name):
			_parent != null ? _parent.resolveFunction(prefix, name): null;
	}
}
