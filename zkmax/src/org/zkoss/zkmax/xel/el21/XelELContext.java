/* XelELContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 15:51:12     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.el21;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.VariableMapper;
import javax.el.ValueExpression;

import org.zkoss.xel.XelContext;

/**
 * An EL context that is based on XEL context.
 *
 * @author tomyeh
 * @since 3.0.0
 */
/*package*/ class XelELContext extends ELContext {
	private final XelContext _xelc;
	private final ELResolver _resolver;

	private static final VariableMapper EMPTY_VAR_MAPPER =
		new VariableMapper() {
			public ValueExpression resolveVariable(String variable) {
				return null;
			}
			public ValueExpression setVariable(String variable,
			ValueExpression expression) {
				throw new UnsupportedOperationException();
			}
		};

	/*package*/ XelELContext(XelContext xelc) {
		_xelc = xelc;
		_resolver = new XelELResolver(xelc.getVariableResolver());
	}

	public ELResolver getELResolver() {
		return _resolver;
	}
	public javax.el.FunctionMapper getFunctionMapper() {
		return _xelc != null ?
			new XelELMapper(_xelc.getFunctionMapper()): null;
	}
	public VariableMapper getVariableMapper() {
		return EMPTY_VAR_MAPPER; //not support
	}
}
