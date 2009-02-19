/* SelectedListModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jan 29 17:29:44     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zul.ListModel;

/**
 * This interface defines the methods used by DataBinder.
 *
 * @author Henri
 * @see BindingListModelList
 * @see BindingListModelSet
 * @see BindingListModelMap
 * @see org.zkoss.zul.ListModel
 */
public interface BindingListModel extends ListModel {
	/** Returns index of the given object inside a ListModel.
	 */
	public int indexOf(Object obj);
}
