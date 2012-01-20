/* XelELContext.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 20 10:15:21     2011, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.xel.zel;

import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.VariableMapper;
import org.zkoss.zel.ValueExpression;

import org.zkoss.xel.XelContext;

/**
 * An ZEL context that is based on XEL context.
 *
 * @author henrichen
 * @since 6.0.0
 */
public class XelELContext extends ELContext {
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

	public XelELContext(XelContext xelc) {
		_xelc = xelc;
		_resolver = newELResolver(xelc);
	}

	protected ELResolver newELResolver(XelContext xelc) {
		return new XelELResolver(xelc);
	}
	public ELResolver getELResolver() {
		return _resolver;
	}
	public org.zkoss.zel.FunctionMapper getFunctionMapper() {
		return _xelc != null ?
			new XelELMapper(_xelc.getFunctionMapper()): null;
	}
	public VariableMapper getVariableMapper() {
		return EMPTY_VAR_MAPPER; //not support
	}
	
	protected XelContext getXelContext() {
		return _xelc;
	}
}
