/* GroupsModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  1 10:10:34     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * The interface defines a suitable data model for grouping {@link Listbox}
 * and {@link Grid}.
 *
 * <p>If the data model is used with sortable listbox or grid,
 * the developer must also implement {@link GroupsModelExt}.
 *
 * @author tomyeh
 * @since 3.5.0
 * @see GroupsModelExt
 * @see ListModel
 */
public interface GroupsModel {
	/** Returns the group value at the specified index.
	 * It is used to render {@link Group} and {@link Listgroup}.
	 */
	public Object getGroup(int index);
	/** Returns the number of groups.
	 */
	public int getGroupCount();

	/** Returns the child value of the specified group at the specified index.
	 */
	public Object getChild(Object group, int index);
	/** Returns the number of children of the specified group.
	 * <p>Note: it does <i>not</i> include the groot foot ({@link #getGroupfoot}).
	 */
	public int getChildCount(Object group);

	/** Returns the foot value of the specified group.
	 * It is used to render {@link Groupfoot} and {@link Listgroupfoot}.
	 *
	 * <p>Note: it is ignored if {@link #hasGroupfoot} returns false.
	 */
	public Object getGroupfoot(Object group);
	/** Returns if the specified group has a foot value.
	 */
	public boolean hasGroupfoot(Object group);

	/** Adds a listener to the groups that's notified each time a change
	 * to the data model occurs. 
	 */
	public void addGroupsDataListener(GroupsDataListener l);
    /** Removes a listener from the groups that's notified each time
     * a change to the data model occurs. 
     */
	public void removeGroupsDataListener(GroupsDataListener l) ;
}
