/* Style.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:31:44     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

/**
 * The STYLE tag.
 * 
 * @author tomyeh
 */
public class Style extends org.zkoss.zhtml.impl.ContentTag {
	public Style() {
		super("style");
	}
	public Style(String content) {
		super("style", content);
	}
}
