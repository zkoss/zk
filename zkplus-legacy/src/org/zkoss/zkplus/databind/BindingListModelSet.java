/* BindingListModelSet.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 29 21:07:15     2007, Created by henrichen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.util.Set;

import org.zkoss.zul.ListModelSet;

/**
 * <p>This is the {@link BindingListModel} as a {@link java.util.Set} to be used with 
 * {@link org.zkoss.zul.Listbox}, {@link org.zkoss.zul.Grid}, 
 * and {@link DataBinder}.
 * Add or remove the contents of this model as a Set would cause the associated Listbox or Grid to change accordingly.</p> 
 * <p>Make as public class since 3.0.5</p>
 *<p>Support BindingListModelEx since 3.1</p>
 * 
 * @author Henri Chen
 * @see BindingListModel
 * @see org.zkoss.zul.ListModel
 * @see org.zkoss.zul.ListModelSet
 * @deprecated As of release 7.0.0, replace with new ZK binding.
 */
public class BindingListModelSet<E> extends ListModelSet<E> implements BindingListModelExt<E>, java.io.Serializable {
	private static final long serialVersionUID = 200808191422L;

	/**
	 * @since 3.0.5.
	 */
	public BindingListModelSet(Set<E> set, boolean live) {
		super(set, live);
	}

	//Set is naturally distinct
	public boolean isDistinct() {
		return true;
	}

	public int[] indexesOf(Object elm) {
		final int idx = indexOf(elm);
		return idx < 0 ? new int[0] : new int[] { idx };
	}
}
