/* Bandbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A band box. A bank box consists of an input box ({@link Textbox} and a popup
 * window {@link Bandpopup}. It is similar to {@link Combobox} except the popup
 * window could have any kind of children. For example, you could place a
 * textbox in the popup to let user search particular items.
 * 
 * <p>
 * Default {@link #getZclass}: z-bandbox.(since 3.5.0)
 * 
 * <p>
 * Events: onOpen<br/>
 * Developers can listen to the onOpen event and initializes it when
 * {@link org.zkoss.zk.ui.event.OpenEvent#isOpen} is true, and/or clean up if
 * false.
 * 
 * <p>
 * Note: to have better performance, onOpen is sent only if a non-deferrable
 * event listener is registered (see {@link org.zkoss.zk.ui.event.Deferrable}).
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Bandbox extends Textbox {

	/**
	 * Returns the dropdown window belonging to this band box.
	 */
	public org.zkoss.zul.api.Bandpopup getDropdownApi();

	/**
	 * Sets whether to automatically drop the list if users is changing this
	 * text box.
	 */
	public void setAutodrop(boolean autodrop);

	/**
	 * Returns whether the button (on the right of the textbox) is visible.
	 * <p>
	 * Default: true.
	 */
	public boolean isButtonVisible();

	/**
	 * Sets whether the button (on the right of the textbox) is visible.
	 */
	public void setButtonVisible(boolean visible);

	/**
	 * Drops down or closes the child.
	 * 
	 * @see #open
	 * @see #close
	 */
	public void setOpen(boolean open);

	/**
	 * Drops down the child. The same as setOpen(true).
	 * 
	 */
	public void open();

	/**
	 * Closes the child if it was dropped down. The same as setOpen(false).
	 * 
	 */
	public void close();

}
