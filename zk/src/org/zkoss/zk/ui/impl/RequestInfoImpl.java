/* RequestInfoImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Apr 19 13:49:49     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import org.zkoss.util.resource.Locator;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.RequestInfo;

/**
 * An implementation of {@link RequestInfo}.
 *
 * @author tomyeh
 */
public class RequestInfoImpl implements RequestInfo {
	private final WebApp _wapp;
	private final Desktop _desktop;
	private final Session _sess;
	private Locator _locator;
	private final Object _request;

	/** Constructor
	 *
	 * @param wapp the Web application, never null.
	 * @param sess the session, or null if not available.
	 * @param desktop the desktop, or null if not created yet.
	 * @param request the request, or null if not available.
	 * @param locator the locator used to locate taglib and other resources.
	 * If null, wapp is used.
	 */
	public RequestInfoImpl(WebApp wapp, Session sess,
	Desktop desktop, Object request, Locator locator) {
		if (wapp == null)
			throw new IllegalArgumentException("null");
		_wapp = wapp;
		_sess = sess;
		_desktop = desktop;
		_request = request;
		_locator = locator;
	}
	/** Constructor.
	 *
	 * @param desktop the desktop, never null.
	 * @param request the request, or null if not available.
	 * @param locator the locator used to locate taglib and other resources.
	 * If null, wapp is used.
	 */
	public RequestInfoImpl(Desktop desktop, Object request, Locator locator) {
		this(desktop.getWebApp(), desktop.getSession(), desktop, request, locator);
	}
	/** Construcotr.
	 *
	 * @param exec the current execution, never null
	 * @param locator the locator used to locate taglib and other resources.
	 * If null, {@link #getWebApp} is used.
	 */
	public RequestInfoImpl(Execution exec, Locator locator) {
		this(exec.getDesktop(), exec.getNativeRequest(), locator);
	}

	public final WebApp getWebApp() {
		return _wapp;
	}
	public final Session getSession() {
		return _sess;
	}
	public final Desktop getDesktop() {
		return _desktop;
	}
	public final Object getNativeRequest() {
		return _request;
	}
	public final Locator getLocator() {
		return _locator;
	}
	public final void setLocator(Locator locator) {
		_locator = locator;
	}
}
