/* Listfoot.java

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


;
/**
 * A row of {@link Listfooter}.
 * 
 * <p>
 * Like {@link Listhead}, each listbox has at most one {@link Listfoot}.
 * <p>
 * Default {@link #getZclass}: z-listfoot (since 5.0.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Listfoot extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Returns the list box that it belongs to.
	 * <p>
	 * It is the same as {@link #getParent}.
	 */
	public org.zkoss.zul.api.Listbox getListboxApi();

}
