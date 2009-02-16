/* AuShowBusy.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 27, 2007 9:54:04 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to show the busy message such that
 * the user knows the system is busy.
 * 
 * @author jumperchen
 * @since 3.0.2
 */
public class AuShowBusy extends AuResponse {
	public AuShowBusy(String mesg, boolean open) {
		super("showBusy", new String [] {mesg != null ? mesg: "", Boolean.toString(open)});
	}
}
