/* AuSetTitle.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:31:55     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to set the title (of window).
 *  <p>data[0]: the title
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class AuSetTitle extends AuResponse {
	public AuSetTitle(String title) {
		super("title", title);
	}

	/** Default: zk.title (i.e., only one response of this class will
	 * be sent to the client in an execution)
	 * @since 5.0.2
	 */
	public final String getOverrideKey() {
		return "zk.title";
	}
}
