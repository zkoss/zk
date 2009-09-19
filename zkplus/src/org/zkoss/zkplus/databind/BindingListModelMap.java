/* BindingListModelMap.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 29 21:07:15     2007, Created by henrichen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zul.ListModelMap;

import java.util.Map;

/**
 * <p>This is the {@link BindingListModel} as a {@link java.util.Map} to be used with 
 * {@link org.zkoss.zul.Listbox}, {@link org.zkoss.zul.Grid}, 
 * and {@link DataBinder}.
 * Add or remove the contents of this model as a Map would cause the associated Listbox or Grid to change accordingly.</p> 
 * <p>Make as public class since 3.0.5</p>
 * <p>Support BindingListModelEx since 3.1</p>
 *
 * @author Henri Chen
 * @see BindingListModel
 * @see BindingListModelExt
 * @see org.zkoss.zul.ListModel
 * @see org.zkoss.zul.ListModelMap
 */
public class BindingListModelMap extends ListModelMap
implements BindingListModelExt, java.io.Serializable {
	private static final long serialVersionUID = 200808191420L;

	/**
	 * @since 3.0.5
	 */
	public BindingListModelMap(Map map, boolean live) {
		super(map, live);
	}
	
	//Map is naturally distinct
	public boolean isDistinct() {
		return true;
	}
	
	public int[] indexesOf(Object elm) {
		final int idx = indexOf(elm);
		return idx < 0 ? new int[0] : new int[] {idx};
	}
}
