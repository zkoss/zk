/* Tabs.java

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

	/** Returns the alignment of tab.
	 * Reserved for future extension; not supported yet.
	 * @since 3.0.0
	 */
	public String getAlign();

	/** Sets the alignment of tab.
	 * Reserved for future extension; not supported yet.
	 * <p>Default: "start".
	 * @param align must be "start" or "center" or "end".
	 * @since 3.0.0
	 */
	public void setAlign(String align) throws WrongValueException;

}
