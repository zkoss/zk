/* AuExtension.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 11 15:25:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.web.servlet.Charsets;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.http.I18Ns;

/**
 * An AU service is a small Java program that can be plugged
 * into {@link DHtmlUpdateServlet} to extend its functionality.
 * An AU processor is associated with a prefix when plugged into
 * the update servlet. Then, when a request with the prefix is received, 
 * the corresponding AU process is invoked to handle the request).
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface AuExtension {
	/** Initializes the AU extension.
	 * It is called when an extension is added to {@link DHtmlUpdateServlet}.
	 */
	public void init(DHtmlUpdateServlet servlet) throws ServletException;

	/** Destroys the AU extension.
	 * It is called when an extension is removed from {@link DHtmlUpdateServlet},
	 * or when {@link DHtmlUpdateServlet} is being destroyed.
	 */
	public void destroy();

	/** Called by ZK to process the AU request.
	 *
	 * <p>To retrieve the http session, use HttpServletRequest.getSession().
	 * To retrieve the ZK session, use
	 * {@link org.zkoss.zk.ui.Sessions#getCurrent(boolean)}.
	 *
	 * @param request the request (never null).
	 * @param response the response (never null).
	 * @param pi the path info. It includes the prefix when the Au processor
	 * is associated (see {@link DHtmlUpdateServlet#addAuExtension}.
	 * For example, if an AU processor is associated with "/upload", then
	 * pi must start with "/upload". Note: it might end with other string
	 * depending on the URI you generated to the client.
	 */
	public void service(HttpServletRequest request, HttpServletResponse response, String pi)
			throws ServletException, IOException;

	/**
	 * Sets the charset of the response.
	 *
	 * @param session ZK session (might be null).
	 * @param request the request (never null).
	 * @param response the response (never null).
	 * @return an object that must be passed to {@link I18Ns#cleanup} or {@link Charsets#cleanup} (can be null).
	 * @since 9.5.1
	 */
	default Object charsetSetup(Session session, HttpServletRequest request,
	                            HttpServletResponse response) {
		return session != null
				? I18Ns.setup(session, request, response, "UTF-8")
				: Charsets.setup(null, request, response, "UTF-8");
	}
}
