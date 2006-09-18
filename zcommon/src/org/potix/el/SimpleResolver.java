/* SimpleResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 28 15:15:04     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.el;

import java.util.Map;

import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.ELException;

/**
 * A simple resolver that retrieve variable from a map.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class SimpleResolver implements VariableResolver {
	/** The parent resolver. */
	protected final VariableResolver _parent;
	/** The variable maps. */
	protected final Map _vars;

	/** Constructs a resolver. */
	public SimpleResolver() {
		this(null, null);
	}
	/** Constructs a resolver with a parent.
	 * @param parent the parent resolver (null means ignored).
	 */
	public SimpleResolver(VariableResolver parent) {
		this(parent, null);
	}
	/** Constructs a resolver with a parent and an object map.
	 * @param parent the parent resolver (null means ignored).
	 * @param vars the object map (null means ignored)
	 */
	public SimpleResolver(VariableResolver parent, Map vars) {
		_parent = parent;
		_vars = vars;
	}
	/** Constructs a resolver with an object map.
	 * @param vars the object map (null means ignored)
	 */
	public SimpleResolver(Map vars) {
		this(null, vars);
	}

	//-- VariableResolver --//
	public Object resolveVariable(String name) throws ELException {
		if (_vars != null) {
			final Object o = _vars.get(name);
			if (o != null)
				return o;
		}
		return _parent != null ? _parent.resolveVariable(name): null;
	}
}
