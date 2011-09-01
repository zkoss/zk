/* AuExtensionRequest.java

	Purpose:
		
	Description:
		
	History:
		Sat Jul  4 17:12:22     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Session;

/**
 * Represents a request for an AU extension ({@link AuExtension}).
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface AuExtensionRequest {
	/** Returns the session, or null if no session and <code>create</code>
	 * is false.
	 *
	 * @param create whether to create one if not available.
	 */
	public Session getSession(boolean create);
	/** Returns the request (never null).
	 */
	public HttpServletRequest getRequest();
	/** Returns the response (never null).
	 */
	public HttpServletResponse getResponse();

	/** Returns the path info of the request.
	 * It includes the prefix when the Au extension
	 * is associated (see {@link DHtmlUpdateServlet#addAuExtension}.
	 * For example, if an AU processor is assoicated with "/upload", then
	 * it must start with "/upload". Note: it might end with other string
	 * depending on the URI you generated to the client.
	 */
	public String getPathInfo();
}
