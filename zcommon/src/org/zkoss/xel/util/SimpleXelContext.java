/* SimpleXelContext.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 16:56:50     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.util;

import org.zkoss.xel.XelContext;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.FunctionMapper;

/**
 * A simple implementation of {@link XelContext}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class SimpleXelContext implements XelContext {
	private VariableResolver _resolver;
	private FunctionMapper _mapper;

	public SimpleXelContext(VariableResolver resolver, FunctionMapper mapper) {
		_resolver = resolver;
		_mapper = mapper;
	}
	public SimpleXelContext(VariableResolver resolver) {
		_resolver = resolver;
	}
	public SimpleXelContext() {
	}

	/** Sete the variable resovler, or null if not available.
	 */
	public void setVariableResolver(VariableResolver resolver) {
		_resolver = resolver;
	}
	/** Sets the function mapper, or null if not available.
	 */
	public void setFunctionMapper(FunctionMapper mapper) {
		_mapper = mapper;
	}

	//XelContext//
	public VariableResolver getVariableResolver() {
		return _resolver;
	}
	public FunctionMapper getFunctionMapper() {
		return _mapper;
	}
}
