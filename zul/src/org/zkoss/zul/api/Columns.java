/* Columns.java

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
 * Defines the columns of a grid. Each child of a columns element should be a
 * {@link Column} element.
 * <p>
 * Default {@link #getZclass}: z-columns.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Columns extends org.zkoss.zul.impl.api.HeadersElement {

	/**
	 * Returns the grid that it belongs to.
	 * <p>
	 * It is the same as {@link #getParent}.
	 */
	public org.zkoss.zul.api.Grid getGridApi();

	/**
	 * Sets whether to enable hiding of columns with the header context menu.
	 * <p>
	 * Note that it is only applied when {@link #getMenupopup()} is auto.
	 * 
	 */
	public void setColumnshide(boolean columnshide);

	/**
	 * Returns whether to enable hiding of columns with the header context menu.
	 * <p>
	 * Default: true.
	 * 
	 */
	public boolean isColumnshide();

	/**
	 * Sets whether to enable grouping of columns with the header context menu.
	 * <p>
	 * Note that it is only applied when {@link #getMenupopup()} is auto.
	 * 
	 */
	public void setColumnsgroup(boolean columnsgroup);

	/**
	 * Returns whether to enable grouping of columns with the header context
	 * menu.
	 * <p>
	 * Default: true.
	 * 
	 */
	public boolean isColumnsgroup();

	/**
	 * Returns the ID of the Menupopup ({@link Menupopup}) that should appear
	 * when the user clicks on the element.
	 * 
	 * <p>
	 * Default: none (a default menupoppup).
	 * 
	 */
	public String getMenupopup();

	/**
	 * Sets the ID of the menupopup ({@link Menupopup}) that should appear when
	 * the user clicks on the element of each column.
	 * 
	 * <p>
	 * An onOpen event is sent to the popup menu if it is going to appear.
	 * Therefore, developers can manipulate it dynamically (perhaps based on
	 * OpenEvent.getReference) by listening to the onOpen event.
	 * 
	 * <p>
	 * Note: To simplify the use, it ignores the ID space when locating the
	 * component at the client. In other words, it searches for the first
	 * component with the specified ID, no matter it is in the same ID space or
	 * not.
	 * 
	 * <p>
	 * If there are two components with the same ID (of course, in different ID
	 * spaces), you can specify the UUID with the following format:<br/>
	 * <code>uuid(comp_uuid)</code>
	 * 
	 * @param mpop
	 *            an ID of the menupopup component, "none", or "auto". "none" is
	 *            assumed by default, "auto" means the menupopup component is
	 *            created automatically.
	 * @see #setMenupopup(String)
	 */
	public void setMenupopup(String mpop);

	/**
	 * Sets the UUID of the popup menu that should appear when the user clicks
	 * on the element.
	 * 
	 * <p>
	 * Note: it actually invokes
	 * <code>setMenupopup("uuid(" + menupop.getUuid() + ")")</code>
	 * 
	 * @see #setMenupopup(String)
	 */
	public void setPopupApi(org.zkoss.zul.api.Menupopup mpop);

}
