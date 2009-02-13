/* ForwardServlet.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	90/12/13 11:30:35, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;

import org.zkoss.lang.D;
import org.zkoss.util.logging.Log;

import org.zkoss.web.servlet.http.Https;

/**
 * The servlet used to forward a request to another URL.
 * The forward target is specified as init-param, so you might need to
 * create and name one servlet for each target.
 *
 * <p>init-param:
 *
 * <dl>
 *  <dt>target</dt>
 *  <dd>The target URL.</dd>
 * </dl>
 *
 * @author tomyeh
 */
public class ForwardServlet extends GenericServlet {
	private static final Log log = Log.lookup(ForwardServlet.class);

	/** The target URL. */
	private String _target;

	public ForwardServlet() {
	}

	//-- super --//
	public void init() throws ServletException {
		_target = getInitParameter("target");
		if (_target == null || _target.length() == 0)
			throw new ServletException("The init param, target, required");
	}

	public void service(ServletRequest request, ServletResponse response)
	throws ServletException, IOException {
		try {
			final String uri =
				Https.locate(getServletContext(), request, _target, null);
			Https.forward(getServletContext(), request, response, uri, null, 0);
		} catch(Error ex) {
			if (D.ON && log.debugable())
				log.realCause(ex);
			throw ex;
		} catch(RuntimeException ex) {
			if (D.ON && log.debugable())
				log.realCause(ex);
			throw ex;
		}
	}
}
