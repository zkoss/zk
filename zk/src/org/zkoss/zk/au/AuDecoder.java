/* AuDecoder.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 20 19:48:15 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.au;

import java.util.List;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;

/**
 * Used to decode the custom format of the AU requests.
 * By default, the AU request is sent in the JSON format.
 * If you prefer to use another format, you have to do as follows.
 * <ul>
 *  <li>Implement this interface to decode the custom format.</li>
 *  <li>Register the implementation by specifying it in <code>WEB-INF/zk.xml</code></li>
 *  <li>Override a JavaScript method called <a href="http://www.zkoss.org/javadoc/latest/jsdoc/_global_/zAu.html#encode%28int,%20zk.Event,%20zk.Desktop%29">zAu.encode()</a>
 *   to encode to the custom format</li>
 * </ul>
 * @author tomyeh
 * @since 5.0.4
 */
public interface AuDecoder {
	/** Returns the desktop ID.
	 * @param request the request. For HTTP, it is HttpServletRequest.
	 */
	public String getDesktopId(Object request);
	/** Returns the first command.
	 * It is called if a desktop is not found.
	 * @param request the request. For HTTP, it is HttpServletRequest.
	 */
	public String getFirstCommand(Object request);
	/** Returns a list of {@link AuRequest} by decoding the request.
	 * @param request the request. For HTTP, it is HttpServletRequest.
	 */
	public List<AuRequest> decode(Object request, Desktop desktop);
	/** Returns if the request is ignorable when an error occurs.
	 * If true is returned, the request is simply ignored.
	 * Otherwise, an error message, depending on the configuration,
	 * is sent to the client.
	 */
	public boolean isIgnorable(Object request, WebApp wapp);
}
