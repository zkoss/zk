/* AuRedirect.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 29, 2013 5:45:24 PM, Created by Vincent
}}IS_NOTE

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zk.au.http;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.AuWriters;
import org.zkoss.zk.au.out.AuSendRedirect;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * The AU processor to handle the HTTP redirect via status 302.
 * 
 * @author Vincent, Robert Wenzel
 * @since 7.0.0
 */
public class AuRedirect implements AuExtension, WebAppInit {

	private static final Logger log = LoggerFactory.getLogger(AuRedirect.class);
	public static final String URI_PREFIX = "/redirect";
	public static final String REDIRECT_URL_PARAMETER = "redirectUrl";

	public void init(WebApp wapp) throws Exception {
		if (DHtmlUpdateServlet.getAuExtension(wapp, URI_PREFIX) == null) {
			try {
				DHtmlUpdateServlet.addAuExtension(wapp, URI_PREFIX, this);
			} catch (Throwable ex) {
				log.error("could not initialize AuRedirect extension", ex);
				throw new IllegalStateException("could not initialize AuRedirect extension", ex);
			}
		}
	}

	public void init(DHtmlUpdateServlet servlet) throws ServletException {
	}

	public void destroy() {
	}

	public void service(HttpServletRequest request, HttpServletResponse response, String pi)
			throws ServletException, IOException {
		String redirectUrl = request.getParameter(REDIRECT_URL_PARAMETER);
		AuSendRedirect auSendRedirect = new AuSendRedirect(redirectUrl, null);
		AuWriter auWriter = AuWriters.newInstance();
		auWriter.open(request, response);
		auWriter.write(auSendRedirect);
		auWriter.close(request, response);
	}
}
