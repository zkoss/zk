/* AuMoveTo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 28 16:12:26     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to move the desktop (aka., the browser window)
 * to specified location (in pixel).
 *
 * <p>data[0]: x<br/>
 * data[1]: y
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuMoveTo extends AuResponse {
	public AuMoveTo(int x, int y) {
		super("moveTo", new String[] {Integer.toString(x), Integer.toString(y)});
	}
}
