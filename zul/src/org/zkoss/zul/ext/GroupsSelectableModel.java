/* GroupsSelectableModel.java

	Purpose:
		
	Description:
		
	History:
		3:00 PM 8/19/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.ext;

import org.zkoss.zul.GroupsModel;

/**Indicate a groups model that supports selection. It is typically used with
 * {@link GroupsModel}.
 *
 * @author jumperchen
 * @since 8.0.0
 */
public interface GroupsSelectableModel<E> extends Selectable<E> {
	/**
	 * Sets the groups are selectable
	 * @param groupSelectable
	 */
	public void setGroupSelectable(boolean groupSelectable);

	/**
	 * Returns whether the groups are selectable
	 */
	public boolean isGroupSelectable();
}
