/* InterpretResolver.java

	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 17:03:58     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.zkoss.lang.D;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.Evaluators;
import org.zkoss.web.servlet.dsp.*;
import org.zkoss.web.servlet.dsp.action.ActionContext;

/**
 * The resolver used to interpret an {@link Interpretation}.
 *
 * @author tomyeh
 */
class InterpretResolver implements VariableResolver {
	private final VariableResolver _parent;
	private final Map _attrs = new HashMap();

	InterpretResolver(VariableResolver parent) {
		assert D.OFF || !(parent instanceof InterpretResolver);
		_parent = parent;
	}

	/** Returns the attributes of the given scope. */
	Map getAttributes(int scope) throws XelException {
		if (scope == ActionContext.PAGE_SCOPE)
			return _attrs;

		Map attrs = null;
		if (_parent != null) {
			switch (scope) {
			case ActionContext.REQUEST_SCOPE:
				attrs = (Map)Evaluators.resolveVariable(_parent, "requestScope");
				break;
			case ActionContext.SESSION_SCOPE:
				attrs = (Map)Evaluators.resolveVariable(_parent, "sessionScope");
				break;
			case ActionContext.APPLICATION_SCOPE:
				attrs = (Map)Evaluators.resolveVariable(_parent, "applicationScope");
				break;
			}
		}
		return attrs != null ? attrs: Collections.EMPTY_MAP;
	}

	//-- VariableResolver --//
	public Object resolveVariable(String name) throws XelException {
		if ("pageScope".equals(name))
			return _attrs;
		final Object o = _attrs.get(name);
		return o != null ? o: Evaluators.resolveVariable(_parent, name);
	}
}
