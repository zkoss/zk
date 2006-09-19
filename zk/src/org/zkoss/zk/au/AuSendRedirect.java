/* AuSendRedirect.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:43:36     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

/**
 * A response to send a temporary redirect response to the client
 * using the specified redirect location URL.
 * <p>data[0]: the URL to redirect to.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuSendRedirect extends AuResponse {
	/**
	 * @param url the target URL, never null.
	 * @param target the target browser window, or null to use the current one.
	 */
	public AuSendRedirect(String url, String target) {
		super("redirect", new String[] {url, target != null ? target: ""});
	}
}
