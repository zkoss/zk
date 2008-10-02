/* ContentRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct  1 18:09:04     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

/**
 * Used with {@link org.zkoss.zk.ui.AbstractComponent#renderContent}
 * to generate the component content that will be sent to the client.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface ContentRenderer {
	/** Renders a property. */
	public void render(String name, String value);
}
