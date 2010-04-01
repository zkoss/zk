/* AuSendRedirect.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:43:36     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to send a temporary redirect response to the client
 * using the specified redirect location URL.
 *
 * <p>data[0]: the URL to redirect to.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class AuSendRedirect extends AuResponse {
	/**
	 * @param url the target URL, never null.
	 * @param target the target browser window, or null to use the current one.
	 */
	public AuSendRedirect(String url, String target) {
		super("redirect", new String[] {url, target != null ? target: ""});
	}

	/** Default: zk.redirect (i.e., only one response of this class will
	 * be sent to the client in an execution)
	 * @since 5.0.2
	 */
	public final String getOverrideKey() {
		return "zk.redirect";
	}
}
