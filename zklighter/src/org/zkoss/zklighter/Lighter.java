/* Lighter.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 22 11:06:33     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zklighter;

/**
 * The entry of ZK Lighter
 *
 * @author tomyeh
 */
public class Lighter {
	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("ZK Lighter - ZK Light JavaScript/CSS Generator\n\n"
			+"Usage:\n\tjava -classpath $CP org.zkoss.zklighter.Lighter js.lst css.lst lib\n");
			System.exit(-1);
		}
	}
}
