/* SessionResolverImpl.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep  2 16:24:36     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.SessionResolver;

/**
 * An implementation of session resolver ({@link SessionResolver}) based on HTTP request.
 * @author tomyeh
 * @since 5.0.0
 */
public class SessionResolverImpl implements SessionResolver {
	private final ServletContext _ctx;
	private final HttpServletRequest _req;

	public SessionResolverImpl(ServletContext ctx, HttpServletRequest request) {
		_ctx = ctx;
		_req = request;
	}
	public Session getSession(boolean create) {
		return WebManager.getSession(_ctx, _req, create);
	}
}
