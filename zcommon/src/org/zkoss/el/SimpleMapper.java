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
package org.zkoss.el;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;

import org.zkoss.util.resource.Locator;
import org.zkoss.idom.Element;
import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelException;
import org.zkoss.el.impl.MethodFunction;

/**
 * A simple function mapper.
 *
 * @author tomyeh
 */
public class SimpleMapper implements FunctionMapper {
	private final FunctionMapper _parent;
	/** Map(String prefix, Map(String, Function)). */
	private final Map _maps = new HashMap();

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
	throws XelException, IOException {
		if (prefix == null || uri == null)
			throw new IllegalArgumentException("null");
		if (_maps.containsKey(prefix))
			throw new XelException("The prefix, "+prefix+", is already used");

		final URL url = locator.getResource(uri);
		if (url == null)
			throw new FileNotFoundException(uri);

		try {
			_maps.put(prefix,
				toMethodFunction(FunctionMappers.loadMethods(url)));
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new XelException(ex);
		}
	}
	/** Converts a map of (any, Method) to (any, {@link MethodFunction}).
	 */
	private static Map toMethodFunction(Map mtds) {
		for (Iterator it = mtds.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final Method mtd = (Method)me.getValue();
			if (mtd != null) me.setValue(new MethodFunction(mtd));
		}
		return mtds;
	}

	/** Loads function definitions from DOM. */
	public void load(String prefix, Element root)
	throws XelException, IOException {
		if (prefix == null || root == null)
			throw new IllegalArgumentException("null");
		if (_maps.containsKey(prefix))
			throw new XelException("The prefix, "+prefix+", is already used");

		try {
			_maps.put(prefix,
				toMethodFunction(FunctionMappers.loadMethods(root)));
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new XelException(ex);
		}
	}

	//-- FunctionMapper --//
	public Function resolveFunction(String prefix, String name) {
		final Map mtds = (Map)_maps.get(prefix);
		return mtds != null ? (Function)mtds.get(name):
			_parent != null ? _parent.resolveFunction(prefix, name): null;
	}
	public Collection getClassNames() {
		return Collections.EMPTY_LIST;
	}
	public Class resolveClass(String name) throws XelException {
		return null;
	}
}
