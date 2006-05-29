/* SimpleMapper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 12:43:56     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.el;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;

import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.ELException;

import com.potix.util.resource.Locator;
import com.potix.idom.Element;

/**
 * A simple function mapper.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.7 $ $Date: 2006/05/29 04:27:17 $
 */
public class SimpleMapper implements FunctionMapper {
	private final FunctionMapper _parent;
	/** Map(String prefix, Map(String, Method)). */
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
	throws ELException, IOException {
		if (prefix == null || uri == null)
			throw new IllegalArgumentException("null");
		if (_maps.containsKey(prefix))
			throw new ELException("The prefix, "+prefix+", is already used");

		final URL url = locator.getResource(uri);
		if (url == null)
			throw new FileNotFoundException(uri);

		try {
			_maps.put(prefix, FunctionMappers.loadMethods(url));
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ELException(ex);
		}
	}
	/** Loads function definitions from DOM. */
	public void load(String prefix, Element root)
	throws ELException, IOException {
		if (prefix == null || root == null)
			throw new IllegalArgumentException("null");
		if (_maps.containsKey(prefix))
			throw new ELException("The prefix, "+prefix+", is already used");

		try {
			_maps.put(prefix, FunctionMappers.loadMethods(root));
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ELException(ex);
		}
	}

	//-- FunctionMapper --//
	public Method resolveFunction(String prefix, String name) {
		final Map mtds = (Map)_maps.get(prefix);
		return mtds != null ? (Method)mtds.get(name):
			_parent != null ? _parent.resolveFunction(prefix, name): null;
	}
}
