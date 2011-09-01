/* TemporaryExecution.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  6 18:38:54     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.util.Map;
import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;

/**
 * A donut execution that is a temporary execution for creating a page
 * or a desktop. It is not a real execution so any update to it are ignored.
 *
 * @author tomyeh
 */
/*package*/ class TemporaryExecution extends ExecutionImpl {
	/** Constructor.
	 */
	/*package*/ TemporaryExecution(ServletContext ctx, HttpServletRequest request,
	HttpServletResponse response, Desktop desktop) {
		super(ctx, request, response, desktop, null);
	}
	public void sendRedirect(String uri) { //getUiEngine not ready yet
		try {
			((HttpServletResponse)getNativeResponse()).sendRedirect(
				uri != null ? uri: "");
			setVoided(true);
		} catch (IOException ex) {
			throw new UiException(ex);
		}
	}
	public void sendRedirect(String uri, String target) {
		sendRedirect(uri); //target is ignored (not supported)
	}
	public void forward(Writer out, String page, Map params, int mode)
	throws IOException {
		final Execution exec = ExecutionsCtrl.getCurrent();
		ExecutionsCtrl.setCurrent(null);
			//It is fake one and shall not be re-used by forward
		try {
			super.forward(out, page, params, mode);
		} finally {
			ExecutionsCtrl.setCurrent(exec);
		}
	}
	public void include(Writer out, String page, Map params, int mode)
	throws IOException {
		throw new IllegalStateException("include not allowd in DesktopInit");
	}
}
