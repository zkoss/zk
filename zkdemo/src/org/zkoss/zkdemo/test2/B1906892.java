/* B1906892.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar  5 11:36:59     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

/**
 * Used to retrieve special characters.
 *
 * @author tomyeh
 */
public class B1906892 {
	public static final String CDATA_BEGIN = "!<[CDATA[";
	public static final String CDATA_END = "]]>";
	public static final String Cx5 = "C" + (char)5;
	public static final String CxD = "C" + (char)0xd;
}
