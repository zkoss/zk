/* Caption.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A header for a {@link Groupbox}. It may contain either a text label, using
 * {@link #setLabel}, or child elements for a more complex caption.
 * <p>
 * Default {@link #getZclass}: z-caption.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Caption extends org.zkoss.zul.impl.api.LabelImageElement {
	/**
	 * Returns a compound label, which is the catenation of parent's title, if
	 * the parent is {@link Window}, and {@link #getLabel}.
	 * <p>
	 * Note: this is designed to used for component templating. Application
	 * developers rarely need to access this method.
	 * 
	 * <p>
	 * It is mainly used for component implementation.
	 */
	public String getCompoundLabel();

	/**
	 * Returns whether the legend mold shall be used. It actually returns
	 * {@link Groupbox#isLegend} if the parent is a {@link Groupbox}.
	 * 
	 * <p>
	 * Note: this is designed to used for component templating. Application
	 * developers rarely need to access this method.
	 */
	public boolean isLegend();

	/**
	 * Returns whether to display the closable button.
	 * <p>
	 * Default: it returns true if the parent is window and
	 * {@link Window#isClosable} is true.
	 * 
	 * <p>
	 * It is mainly used for component implementation.
	 */
	public boolean isClosableVisible();

	/**
	 * Returns whether to display the maximizable button.
	 * <p>
	 * Default: it returns true if the parent is window and
	 * {@link Window#isMaximizable} is true.
	 * 
	 * <p>
	 * It is mainly used for component implementation.
	 * 
	 */
	public boolean isMaximizableVisible();

	/**
	 * Returns whether to display the minimizable button.
	 * <p>
	 * Default: it returns true if the parent is window and
	 * {@link Window#isMinimizable} is true.
	 * 
	 * <p>
	 * It is mainly used for component implementation.
	 * 
	 */
	public boolean isMinimizableVisible();
}
