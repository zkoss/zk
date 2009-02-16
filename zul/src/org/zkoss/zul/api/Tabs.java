/* Tabs.java

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
 * A collection of tabs ({@link Tab}).
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Tabs extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the tabbox owns this component.
	 * <p>
	 * It is the same as {@link #getParent}.
	 */
	public org.zkoss.zul.api.Tabbox getTabboxApi();

	/**
	 * Returns the alignment of tab. (not supported in mold accordion and
	 * version 3.5)
	 * <p>
	 * Default: "start".
	 * 
	 * <p>
	 * Note: only the default mold supports it (not supported in mold accordion
	 * and version 3.5).
	 */
	public String getAlign();

	/**
	 * Sets the alignment of tab. (not supported in mold accordion and version
	 * 3.5)
	 * 
	 * @param align
	 *            must be "start" or "center" or "end".
	 */
	public void setAlign(String align) throws WrongValueException;

}
