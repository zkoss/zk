/* AuProcessor.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 11 15:25:38     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Session;

/**
 * An AU processor that can be plugged into {@link DHtmlUpdateServlet}.
 * An AU processor is associated with a prefix when plugged into
 * the update servlet. Then, when a request with the prefix is received, 
 * the corresponding AU process is invoked to handle the request).
 *
 * @author tomyeh
 * @since 3.0.2
 */
public interface AuProcessor {
	/** Called to process the request if the session is available.
	 *
	 * @param sess the session, or null if the session is not available.
	 * @param ctx the servlet context (never null).
	 * @param request the request (never null).
	 * @param response the response (never null).
	 * @param pi the path info. It includes the prefix when the Au processor
	 * is associated (see {@link DHtmlUpdateServlet#addAuProcessor}.
	 * For example, if an AU processor is assoicated with "/upload", then
	 * pi must start with "/upload". Note: it might end with other string
	 * depending on the URI you generated to the client.
	 */
	public void process(Session sess, ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response, String pi)
	throws ServletException, IOException;
}
