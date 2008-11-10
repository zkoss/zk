/* Rows.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.List;
import java.util.Iterator;

/**
 * Defines the rows of a grid. Each child of a rows element should be a
 * {@link Row} element.
 * <p>
 * Default {@link #getZclass}: z-rows.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Rows extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Returns the grid that contains this rows.
	 * <p>
	 * It is the same as {@link #getParent}.
	 */
	public org.zkoss.zul.api.Grid getGridApi();

	/**
	 * Returns the number of groups.
	 * 
	 */
	public int getGroupCount();

	/**
	 * Returns a list of all {@link Group}.
	 * 
	 */
	public List getGroups();

	/**
	 * Returns whether Group exists.
	 * 
	 */
	public boolean hasGroup();

	// Paging//
	/**
	 * Returns the number of visible descendant {@link Row}.
	 * 
	 */
	public int getVisibleItemCount();

	/**
	 * @deprecated As of release As of release 3.5.1
	 */
	public int getVisibleBegin();

	/**
	 * @deprecated As of release As of release 3.5.1
	 */
	public int getVisibleEnd();

	/**
	 * Returns an iterator to iterate thru all visible children. Unlike
	 * {@link #getVisibleItemCount}, it handles only the direct children.
	 * Component developer only.
	 * 
	 */
	public Iterator getVisibleChildrenIterator();

}
