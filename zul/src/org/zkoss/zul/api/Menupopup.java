/* Menupopup.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A container used to display menus. It should be placed inside a {@link Menu}.
 * 
 * <p>
 * Supported event: onOpen.<br/>
 * Note: to have better performance, onOpen is sent only if non-deferrable event
 * listener is registered (see {@link org.zkoss.zk.ui.event.Deferrable}).
 * 
 * <p>
 * To load the content dynamically, you can listen to the onOpen event, and then
 * create menuitem when {@link org.zkoss.zk.ui.event.OpenEvent#isOpen} is true.
 * 
 * <p>
 * Default {@link #getZclass}: z-menu-popup. (since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Menupopup extends Popup {

}
