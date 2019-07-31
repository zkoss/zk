/* SelectedListModel.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 29 17:29:44     2007, Created by henrichen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
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
 * @deprecated As of release 7.0.0, replace with new ZK binding.
 */
public interface BindingListModel<E> extends ListModel<E> {
	/** Returns index of the given object inside a ListModel.
	 */
	public int indexOf(Object obj);
}
