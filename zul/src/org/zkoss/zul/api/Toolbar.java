/* Toolbar.java

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

import org.zkoss.zk.ui.WrongValueException;

/**
 * A toolbar.
 * 
 * <p>
 * Mold:
 * <ol>
 * <li>default</li>
 * <li>panel: since 3.5.0, this mold is used for {@link Panel} component as its
 * foot toolbar.</li>
 * </ol>
 * <p>
 * Default {@link #getZclass}: z-toolbar, if {@link #getMold()} is panel,
 * z-toolbar-panel is assumed.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Toolbar extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the alignment of any children added to this toolbar. Valid values
	 * are "start", "end" and "center".
	 * <p>
	 * Default: "start"
	 * 
	 */
	public String getAlign();

	/**
	 * Sets the alignment of any children added to this toolbar. Valid values
	 * are "start", "end" and "center".
	 * <p>
	 * Default: "start", if null, "start" is assumed.
	 * 
	 */
	public void setAlign(String align);

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

}
