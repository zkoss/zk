/* BindingGroupsListModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 26, 2011 10:47:25 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/


package org.zkoss.zkplus.databind;

import org.zkoss.lang.Objects;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.GroupsListModel;

/**
 * <p>This is the {@link BindingListModel} as a {@link GroupsListModel} to be used with 
 * {@link org.zkoss.zul.Listbox}, {@link org.zkoss.zul.Grid},  
 * and {@link DataBinder}.
 * Add or remove the contents of this model as a GroupListModel would cause the associated Listbox or Grid to change accordingly.</p> 
 *
 * @author henrichen
 * @since 3.6.5
 */
public class BindingGroupsListModel extends GroupsListModel implements BindingListModel, java.io.Serializable {
	public BindingGroupsListModel(GroupsModel model) {
		super(model);
	}

	//--BindingListModel--//
	public int indexOf(Object obj) {
		for(int j = getSize(); j-- > 0;) {
			if (Objects.equals(getElementAt(j), obj)) {
				return j;
			}
		}
		return -1;
	}
}
