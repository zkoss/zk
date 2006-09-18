/* AuMoveTo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 28 16:12:26     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

/**
 * A response to ask the client to move the desktop (aka., the browser window)
 * to specified location (in pixel).
 *
 * <p>data[0]: x<br/>
 * data[1]: y
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class AuMoveTo extends AuResponse {
	public AuMoveTo(int x, int y) {
		super("moveTo", new String[] {Integer.toString(x), Integer.toString(y)});
	}
}
