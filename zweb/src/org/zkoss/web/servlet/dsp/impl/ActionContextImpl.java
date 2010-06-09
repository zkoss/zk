/* ActionContextImpl.java

	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 16:52:35     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import java.util.Map;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.web.servlet.dsp.*;
import org.zkoss.web.servlet.dsp.action.Action;
import org.zkoss.web.servlet.dsp.action.ActionContext;

/**
 * An implementation of {@link ActionContext}.
 *
 * @author tomyeh
 */
class ActionContextImpl implements ActionContext {
	private final InterpretContext _ic;
	private final Action _parent;
	private final ActionNode _current;
	private final int _nLines;
	ActionContextImpl(InterpretContext ic, Action parent,
	ActionNode current, int nLines) {
		_ic = ic;
		_parent = parent;
		_nLines = nLines;
		_current = current;
	}

	//-- ActionContext --//
	public Object getAttribute(String name, int scope) {
		return _ic.resolver.getAttributes(scope).get(name);
	}
	public void setAttribute(String name, Object value, int scope) {
		if (value == null) {
			removeAttribute(name, scope);
			return;
		}
		_ic.resolver.getAttributes(scope).put(name, value);
	}
	public void removeAttribute(String name, int scope) {
		_ic.resolver.getAttributes(scope).remove(name);
	}
	public Object findAttribute(String name) {
		Object o = getAttribute(name, PAGE_SCOPE);
		if (o != null) return o;
		o = getAttribute(name, REQUEST_SCOPE);
		if (o != null) return o;
		o = getAttribute(name, SESSION_SCOPE);
		return o != null ? o: getAttribute(name, APPLICATION_SCOPE);
	}

	public void setContentType(String ctype) {
		_ic.dc.setContentType(ctype);
	}

	public Writer getOut() throws IOException {
		return _ic.dc.getOut();
	}
	public Action getParent() {
		return _parent;
	}

	public void renderFragment(Writer out)
	throws DspException, IOException {
		if (out == null || out == _ic.dc.getOut()) {
			_current.renderFragment(_ic);
		} else {
			final Writer old = _ic.dc.getOut();
			_ic.dc.setOut(out);
			try {
				_current.renderFragment(_ic);
			} finally {
				_ic.dc.setOut(old);
			}
		}
	}
	public void include(String uri, Map params)
	throws DspException, IOException {
		try {
			_ic.dc.include(uri, params);
		} catch (javax.servlet.ServletException ex) {
			throw new DspException(ex);
		}
	}
	public boolean isIncluded() {
		return _ic.dc.isIncluded();
	}

	public String encodeURL(String uri)
	throws DspException, IOException {
		try {
			return _ic.dc.encodeURL(uri);
		} catch (javax.servlet.ServletException ex) {
			throw new DspException(ex);
		}
	}

	public int getLineNumber() {
		return _nLines;
	}
}
