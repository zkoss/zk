/* Groupbox.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * Groups a set of child elements to have a visual effect.
 * <p>
 * Default {@link #getZclass}: "z-fieldset". If {@link #getMold()} is 3d,
 * "z-groupbox" is assumed.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Groupbox extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Returns the caption of this groupbox.
	 */
	public org.zkoss.zul.api.Caption getCaptionApi();

	/**
	 * Returns whether this groupbox is open.
	 * 
	 * <p>
	 * Default: true.
	 */
	public boolean isOpen();

	/**
	 * Opens or closes this groupbox.
	 */
	public void setOpen(boolean open);

	/**
	 * Returns whether user can open or close the group box. In other words, if
	 * false, users are no longer allowed to change the open status (by clicking
	 * on the title).
	 * 
	 * <p>
	 * Default: true.
	 */
	public boolean isClosable();

	/**
	 * Sets whether user can open or close the group box.
	 */
	public void setClosable(boolean closable);

	/**
	 * Returns the CSS style for the content block of the groupbox. Used only if
	 * {@link #getMold} is not default.
	 */
	public String getContentStyle();

	/**
	 * Sets the CSS style for the content block of the groupbox. Used only if
	 * {@link #getMold} is not default.
	 * 
	 * <p>
	 * Default: null.
	 */
	public void setContentStyle(String style);

	/**
	 * Returns the style class used for the content block of the groupbox. Used
	 * only if {@link #getMold} is not default.
	 */
	public String getContentSclass();

	/**
	 * Sets the style class used for the content block.
	 * 
	 * @see #getContentSclass
	 */
	public void setContentSclass(String scls);

	/**
	 * Returns whether this groupbox is in the legend mold. By the legend mold
	 * we mean this group box is rendered with HTML FIELDSET tag.
	 * 
	 * <p>
	 * Default: the legend mold is assumed if {@link #getMold} returns
	 * "default".
	 * 
	 * <p>
	 * If it is not the case, you can call {@link #setLegend} to change it.
	 * 
	 */
	public boolean isLegend();

	/**
	 * Sets whether this groupbox is in the legend mold.
	 * 
	 * @see #isLegend
	 */
	public void setLegend(boolean legend);

}
