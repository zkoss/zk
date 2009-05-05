/* Treecol.java

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

/**
 * A treecol.
 * <p>
 * Default {@link #getZclass}: z-treecol (since 5.0.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Treecol extends org.zkoss.zul.impl.api.HeaderElement {

	/**
	 * Returns the tree that it belongs to.
	 */
	public org.zkoss.zul.api.Tree getTreeApi();

	/**
	 * Returns the maximal length of each item's label.
	 */
	public int getMaxlength();

	/**
	 * Sets the maximal length of each item's label.
	 */
	public void setMaxlength(int maxlength);

	/**
	 * Returns the column index, starting from 0.
	 */
	public int getColumnIndex();

}
