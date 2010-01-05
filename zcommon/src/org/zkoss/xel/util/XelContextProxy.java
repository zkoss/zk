/* XelContextProxy.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 31 15:49:56 TST 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.xel.util;

import java.util.Map;
import org.zkoss.xel.*;

/**
 * A proxy to replace the variable resolver of a given XEL context.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class XelContextProxy implements XelContext {
	private final XelContext _ctx;
	private FunctionMapper _mapper;
	private VariableResolver _resolver;
	private boolean _bResolver, _bMapper;

	/** Replaces the variable resolver with the specified one,
	 * but the function mapper not changed.
	 */
	public XelContextProxy(XelContext ctx, VariableResolver resolver) {
		_ctx = ctx;
		_resolver = resolver;
		_bResolver = true;
	}
	/** Replaces the function mapper with the specified one,
	 * but the variable resolver not changed.
	 */
	public XelContextProxy(XelContext ctx, FunctionMapper mapper) {
		_ctx = ctx;
		_mapper = mapper;
		_bMapper = true;
	}
	/** Replaces both variable resolver and function mapper.
	 */
	public XelContextProxy(XelContext ctx, VariableResolver resolver,
	FunctionMapper mapper) {
		_ctx = ctx;
		_resolver = resolver;
		_mapper = mapper;
		_bResolver = _bMapper = true;
	}

	public VariableResolver getVariableResolver() {
		return _bResolver ? _resolver: _ctx.getVariableResolver();
	}
	public FunctionMapper getFunctionMapper() {
		return _bMapper ? _mapper: _ctx.getFunctionMapper();
	}
	public Object getAttribute(String name) {
		return _ctx.getAttribute(name);
	}
	public Object setAttribute(String name, Object value) {
		return _ctx.setAttribute(name, value);
	}
	public boolean hasAttribute(String name) {
		return _ctx.hasAttribute(name);
	}
	public Object removeAttribute(String name) {
		return _ctx.removeAttribute(name);
	}
	public Map getAttributes() {
		return _ctx.getAttributes();
	}
}
