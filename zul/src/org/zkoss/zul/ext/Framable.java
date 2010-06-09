/* Framable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 3, 2009 5:03:50 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.ext;

/**
 * Indicates a component that can be built-in collapsible, closable,
 * maximizable, and minimizable behavior, along with a variety of pre-built tool 
 * buttons that can be wired up to provide other customized behavior.
 * 
 * @author jumperchen
 * @since 3.6.2
 */
public interface Framable {
	/**
	 * Returns whether to show a close button.
	 */
	public boolean isClosable();
	/**
	 * Returns whether to display the maximizing button.
	 */
	public boolean isMaximizable();
	/**
	 * Returns whether to display the minimizing button.
	 */
	public boolean isMinimizable();
	/**
	 * Returns whether to show a toggle button.
	 */
	public boolean isCollapsible();
	/**
	 * Returns whether is maximized.
	 * @since 3.6.3
	 */
	public boolean isMaximized();

	/**
	 * Returns the title.
	 * @since 3.6.3
	 */
	public String getTitle();
}

