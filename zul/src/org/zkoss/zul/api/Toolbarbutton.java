/* Toolbarbutton.java

{{{IS_NOTE
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

import org.zkoss.zk.ui.WrongValueException;

/**
 * A tool button.
 * 
 * <p>
 * The default CSS class is "button".
 * 
 * <p>
 * Non-xul extension: Toolbarbutton supports {@link #getHref}. If
 * {@link #getHref} is not null, the onClick handler is ignored and this element
 * is degenerated to HTML's A tag.
 * <p>
 * Default {@link #getZclass}: z-toolbar-button.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Toolbarbutton extends org.zkoss.zul.impl.api.LabelImageElement {

	/**
	 * Sets whether it is disabled.
	 * 
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 * 
	 */
	public boolean isDisabled();

	/**
	 * Returns the direction.
	 * <p>
	 * Default: "normal".
	 */
	public String getDir();

	/**
	 * Sets the direction.
	 * 
	 * @param dir
	 *            either "normal" or "reverse".
	 */
	public void setDir(String dir) throws WrongValueException;

	/**
	 * Returns the href.
	 * <p>
	 * Default: null. If null, the button has no function unless you specify the
	 * onClick handler.
	 */
	public String getHref();

	/**
	 * Sets the href.
	 */
	public void setHref(String href) throws WrongValueException;

	/**
	 * Returns the orient.
	 * <p>
	 * Default: "horizontal".
	 */
	public String getOrient();

	/**
	 * Sets the orient.
	 * 
	 * @param orient
	 *            either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException;

	/**
	 * Returns the target frame or window.
	 * 
	 * <p>
	 * Note: it is useful only if href ({@link #setHref}) is specified (i.e.,
	 * use the onClick listener).
	 * 
	 * <p>
	 * Default: null.
	 */
	public String getTarget();

	/**
	 * Sets the target frame or window.
	 * 
	 * @param target
	 *            the name of the frame or window to hyperlink.
	 */
	public void setTarget(String target);

	/**
	 * Returns the tab order of this component.
	 * <p>
	 * Default: -1 (means the same as browser's default).
	 */
	public int getTabindex();

	/**
	 * Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException;

}
