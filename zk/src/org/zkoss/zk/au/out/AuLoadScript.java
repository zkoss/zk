/** AuLoadScript.java.

	Purpose:
		
	Description:
		
	History:
		2:17:18 PM Apr 16, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.au.out;

import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Executions;

/**
 * Loads a JavaScript file to client and execute it.
 * @author jumperchen
 * @since 8.0.0
 */
public class AuLoadScript extends AuResponse {
	/**
	 * Loads a JavaScript file to client and execute it.
	 * 
	 * @param url
	 *            the URL of the JavasScript file, it will be encoded with
	 *            {@link Encodes#encodeURL(jakarta.servlet.ServletContext, jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse, String)}.
	 * @param callback a callback script to execute when the JavaScript file has loaded.
	 * @param once true means the url will be cached and executed only once.
	 */
	public AuLoadScript(String url, String callback, boolean once) {
		super("loadScript", new Object[] { Executions.encodeURL(url), callback, once });
	}
}
