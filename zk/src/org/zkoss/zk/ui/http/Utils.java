/* Utils.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  4 21:36:13     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.mesg.Messages;
import org.zkoss.lang.Exceptions;

import org.zkoss.web.Attributes;
import org.zkoss.web.servlet.Servlets;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.UiException;

/**
 * A collection of utilities for this package.
 *
 * @author tomyeh
 * @since 2.4.1
 */
/*package*/ class Utils {
	/** Handles exception being thrown when rendering a page.
	 * @param ex the exception being throw. If null, it means the page
	 * is not found.
	 */
	/*package*/ static
	void handleError(ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response,
	String path, Throwable err) throws ServletException, IOException {
		if (Servlets.isIncluded(request)) {
			final String msg =
				err != null ?
					Messages.get(MZk.PAGE_FAILED,
						new Object[] {path, Exceptions.getMessage(err),
						Exceptions.formatStackTrace(null, err, null, 6)}):
					Messages.get(MZk.PAGE_NOT_FOUND, new Object[] {path});

			final Map attrs = new HashMap();
			attrs.put(Attributes.ALERT_TYPE, "error");
			attrs.put(Attributes.ALERT, msg);
			Servlets.include(ctx, request, response,
				"~./html/alert.dsp", attrs, Servlets.PASS_THRU_ATTR);
		} else {
			//If not included, let the Web container handle it
			if (err != null) {
				if (err instanceof ServletException)
					throw (ServletException)err;
				else if (err instanceof IOException)
					throw (IOException)err;
				else
					throw UiException.Aide.wrap(err);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND, path);
		}
	}
}
