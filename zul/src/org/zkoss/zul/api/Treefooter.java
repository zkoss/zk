/* Treefooter.java

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
 * A column of the footer of a tree ({@link Tree}). Its parent must be
 * {@link Treefoot}.
 * 
 * <p>
 * Unlike {@link Treecol}, you could place any child in a tree footer.
 * <p>
 * Note: {@link Treecell} also accepts children.
 * <p>
 * Default {@link #getZclass}: z-tree-footer.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Treefooter extends org.zkoss.zul.impl.api.LabelImageElement {

	/**
	 * Returns the tree that this belongs to.
	 */
	public org.zkoss.zul.api.Tree getTreeApi();

	/**
	 * Returns the column index, starting from 0.
	 */
	public int getColumnIndex();

	/**
	 * Returns the tree header that is in the same column as this footer, or
	 * null if not available.
	 */
	public org.zkoss.zul.api.Treecol getTreecolApi();

	/**
	 * Returns number of columns to span this footer. Default: 1.
	 */
	public int getSpan();

	/**
	 * Sets the number of columns to span this footer.
	 * <p>
	 * It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span);

}
