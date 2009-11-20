/* GroupsModel.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep  1 10:10:34     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.GroupsDataListener;

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
	 * @param groupIndex the index of the group.
	 */
	public Object getGroup(int groupIndex);
	/** Returns the number of groups.
	 */
	public int getGroupCount();

	/** Returns the child value of the specified group at the specified index.
	 * @param groupIndex the index of the group.
	 * @param index the index of the element in the group.
	 */
	public Object getChild(int groupIndex, int index);
	/** Returns the number of children of the specified group.
	 * <p>Note: it does <i>not</i> include the groot foot ({@link #getGroupfoot}).
	 * @param groupIndex the index of the group.
	 */
	public int getChildCount(int groupIndex);

	/** Returns the foot value of the specified group.
	 * It is used to render {@link Groupfoot} and {@link Listgroupfoot}.
	 *
	 * <p>Note: it is ignored if {@link #hasGroupfoot} returns false.
	 * @param groupIndex the index of the group.
	 */
	public Object getGroupfoot(int groupIndex);
	/** Returns if the specified group has a foot value.
	 * @param groupIndex the index of the group.
	 */
	public boolean hasGroupfoot(int groupIndex);

	/** Adds a listener to the groups that's notified each time a change
	 * to the data model occurs. 
	 */
	public void addGroupsDataListener(GroupsDataListener l);
    /** Removes a listener from the groups that's notified each time
     * a change to the data model occurs. 
     */
	public void removeGroupsDataListener(GroupsDataListener l) ;
	/** Whether the group is close at the specified index.
	 * It is used to render {@link Group} and {@link Listgroup}.
	 * @param groupIndex the index of the group.
	 * @since 5.0.0
	 */
	public boolean isClose(int groupIndex);
	/** Sets whether the group is close at the specified index.
	 * It is used to render {@link Group} and {@link Listgroup}.
	 * @param groupIndex the index of the group.
	 * @param close true to close the Group.
	 * @since 5.0.0
	 */
	public void setClose(int groupIndex, boolean close);
}
