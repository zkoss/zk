/* AuEcho.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 20 23:28:02     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

/**
 * A response to ask client to send a dummy request back to the server.
 *
 * <p>It is used by {@link org.zkoss.zk.ui.sys.UiEngine} to solve a special
 * case.
 *
 * <p>data[0]: null (ie., empty for client)
 * 
 * @author tomyeh
 */
public class AuEcho  extends AuResponse {
	public AuEcho() {
		super("echo");
	}
}
