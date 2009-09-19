/* AuConfirmClose.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov  6 16:44:28     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to show a confirm dialog when an user tries
 * to close the browser window.
 *
 * <p>data[0]: the message to confirm
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuConfirmClose extends AuResponse {
	public AuConfirmClose(String mesg) {
		super("cfmClose", mesg != null ? mesg: "");
	}
}
