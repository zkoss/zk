/* AuPrint.java

	Purpose:
		
	Description:
		
	History:
		Sat Apr  8 21:08:05     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to print the desktop (aka., the browser window).
 *
 * <p>no data.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuPrint extends AuResponse {
	public AuPrint() {
		super("print");
	}

	/** Default: zk.print (i.e., only one response of this class will
	 * be sent to the client in an execution)
	 * @since 5.0.2
	 */
	public final String getOverrideKey() {
		return "zk.print";
	}
}
