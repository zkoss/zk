/* ClassWebServlet.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 19 20:55:40     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import org.zkoss.lang.D;
import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.http.Https;

/**
 * Loads the resource from the class path starting with "/web".
 *
 * <p>You don't need this servlet if you are using ZK (because
 * DHtmlUpdateServlet implements such function).
 *
 * @author tomyeh
 */
public class ClassWebServlet extends HttpServlet {
	private static final Log log = Log.lookup(ClassWebServlet.class);

	private ServletContext _ctx;
	private String _mappingURI;
	private ClassWebResource _cwr;

	public void init(ServletConfig config) throws ServletException {
		//super.init(config);
			//Note callback super to avoid saving config

		_ctx = config.getServletContext();

		_mappingURI = config.getInitParameter("mapping-uri");
		if (_mappingURI == null || _mappingURI.length() == 0
		|| _mappingURI.charAt(0) != '/')
			throw new ServletException("The mapping-uri parameter must be specified and starts with /");
		if (_mappingURI.charAt(_mappingURI.length() - 1) == '\\') {
			if (_mappingURI.length() == 1)
				throw new ServletException("The mapping-uri parameter cannot contain only /");
			_mappingURI = _mappingURI.substring(0, _mappingURI.length() - 1);
				//remove the trailing '\\' if any
		}

		_cwr = ClassWebResource.getInstance(_ctx, _mappingURI);
	}
	protected
	void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final String pi = Https.getThisPathInfo(request);
		if (pi != null)
			_cwr.service(request, response,
				pi.substring(ClassWebResource.PATH_PREFIX.length()));
		else
			log.error("Path info not specified");
	}
	protected
	void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}
}
