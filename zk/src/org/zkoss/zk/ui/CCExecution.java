/* CCExecution.java

	Purpose:
		
	Description:
		
	History:
		Tue May 12 15:55:52     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;

import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.impl.DesktopImpl;

/**
 * Used for create components and other temporary tasks.
 * @author tomyeh
 */
/*package*/ class CCExecution extends org.zkoss.zk.ui.http.ExecutionImpl {
	private Map<String, Object> _attrs;

	/*package*/ static CCExecution newInstance(WebApp wapp) {
		final ServletContext ctx = wapp.getServletContext();
		WebManager webm = WebManager.getWebManager(ctx);
		final String updateURI = webm.getUpdateURI();
		final String resourceURI = webm.getResourceURI();
		return new CCExecution(ctx, new DesktopImpl(wapp, updateURI, resourceURI, "/", null, null));
	}

	private CCExecution(ServletContext ctx, Desktop desktop) {
		super(ctx, null, null, desktop, null);
		setDesktop(desktop);
	}

	@Override
	public Object getAttribute(String name) {
		return _attrs != null ? _attrs.get(name) : null;
	}

	@Override
	public Object setAttribute(String name, Object value) {
		if (_attrs == null)
			_attrs = new HashMap<>(2);
		return _attrs.put(name, value);
	}

	@Override
	public Object removeAttribute(String name) {
		return _attrs != null ? _attrs.remove(name) : null;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return _attrs != null ? _attrs : Collections.emptyMap();
	}
}
