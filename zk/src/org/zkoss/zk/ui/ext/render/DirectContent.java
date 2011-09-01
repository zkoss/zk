/* DirectContent.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 21 15:24:01     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.ext.render;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * if the component is able to generate the content directly.
 * In other words, it recogizes {@link org.zkoss.zk.ui.sys.HtmlPageRenders#isDirectContent}.
 * 
 * @author tomyeh
 * @since 5.0.0
 */
public interface DirectContent {
}
