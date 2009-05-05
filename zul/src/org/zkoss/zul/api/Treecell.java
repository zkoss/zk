/* Treecell.java

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
 * A treecell.
 * 
 * <p>
 * In XUL, treecell cannot have any child, but ZUL allows it. Thus, you could
 * place any kind of children in it. They will be placed right after the image
 * and label.
 * 
 * <p>
 * Default {@link #getZclass}: z-treecell (since 5.0.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Treecell extends org.zkoss.zul.impl.api.LabelImageElement {

	/**
	 * Return the tree that owns this cell.
	 */
	public org.zkoss.zul.api.Tree getTreeApi();

	/**
	 * Returns the tree col associated with this cell, or null if not available.
	 */
	public org.zkoss.zul.api.Treecol getTreecolApi();

	/**
	 * Returns the column index of this cell, starting from 0.
	 */
	public int getColumnIndex();

	/**
	 * Returns the maximal length for this cell, which is decided by the
	 * corresponding {@link #getTreecolApi}'s {@link Treecol#getMaxlength}.
	 */
	public int getMaxlength();

	/**
	 * Returns the level this cell is. The root is level 0.
	 */
	public int getLevel();

	/**
	 * Returns number of columns to span this cell. Default: 1.
	 */
	public int getSpan();

	/**
	 * Sets the number of columns to span this cell.
	 * <p>
	 * It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span);

}
