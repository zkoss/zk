/* AuLog.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 24 17:51:41 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * Logs the message to the client.
 * @author tomyeh
 * @since 5.0.8
 */
public class AuLog extends AuResponse {
	public AuLog(String msg) {
		super("log", msg);
	}
}
