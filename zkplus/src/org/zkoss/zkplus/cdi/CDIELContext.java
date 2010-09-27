/* CDIELContext.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec 28 11:25:12     2009, Created by henrichen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.cdi;

import java.lang.reflect.Method;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelContext;

/**
 * An EL context that is based on XEL context for use with CDI.
 * <p>Applicable to CDI version 1.0 or later</p>
 * @author henrichen
 */
/*package*/ class CDIELContext extends ELContext {
	private final XelContext _xelc;
	private final ELResolver _cdiResolver;

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
	/*package*/ CDIELContext(final XelContext xelc, ELResolver cdiResolver) {
		_xelc = xelc;
		_cdiResolver = cdiResolver;
	}
	public ELResolver getELResolver() {
		return _cdiResolver;
	}
	public javax.el.FunctionMapper getFunctionMapper() {
		return _xelc != null ?
			new XelELMapper(_xelc.getFunctionMapper()): null;
	}
	public VariableMapper getVariableMapper() {
		return EMPTY_VAR_MAPPER; //not support
	}
	public XelContext getXelContext() {
		return _xelc;
	}
	private class XelELMapper extends javax.el.FunctionMapper {
		private FunctionMapper _mapper;

		public XelELMapper(FunctionMapper mapper) {
			_mapper = mapper;
		}
		public Method resolveFunction(String prefix, String name) {
			if (_mapper != null) {
				final Function f = _mapper.resolveFunction(prefix, name);
				if (f != null)
					return f.toMethod();
			}
			return null;
		}
	}
}
