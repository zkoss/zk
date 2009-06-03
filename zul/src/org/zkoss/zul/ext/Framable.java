/* Framable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2009/6/3 下午 5:07:59 , Created by jumperchen
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
	 * @return
	 */
	public boolean isMinimizable();
	/**
	 * Returns whether to show a toggle button.
	 */
	public boolean isCollapsible();
}

