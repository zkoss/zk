/* BindingListModelList.java

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

import org.zkoss.lang.Objects;
import org.zkoss.zul.ListModelList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>This is the {@link BindingListModel} as a {@link java.util.List} to be used with 
 * {@link org.zkoss.zul.Listbox}, {@link org.zkoss.zul.Grid},  
 * and {@link DataBinder}.
 * Add or remove the contents of this model as a List would cause the associated Listbox or Grid to change accordingly.</p> 
 * <p>Make as public class since 3.0.5</p>
 * <p>Support BindingListModelEx since 3.1</p>
 *
 * @author Henri Chen
 * @see BindingListModel
 * @see org.zkoss.zul.ListModel
 * @see org.zkoss.zul.ListModelList
 */
public class BindingListModelList extends ListModelList
implements BindingListModelExt, java.io.Serializable {
	private static final long serialVersionUID = 200808191518L;
	private boolean _distinct = true; //since 3.5; default to true

	/**
	 * @since 3.1
	 */
	@SuppressWarnings("unchecked")
	public BindingListModelList(List list, boolean live, boolean distinct) {
		super(list, live);
		_distinct = distinct;
	}

	/**
	 * @since 3.0.5
	 */
	@SuppressWarnings("unchecked")
	public BindingListModelList(List list, boolean live) {
		super(list, live);
	}
	
	public boolean isDistinct() {
		return _distinct;
	}
	
	public int[] indexesOf(Object elm) {
		if (isDistinct()) {
			final int idx = indexOf(elm);
			return idx < 0 ? new int[0] : new int[] {idx}; 
		} else {
			final List<Integer> indexes = new LinkedList<Integer>();
			int j = 0;
			for(final Iterator it = _list.iterator(); it.hasNext(); ++j) {
				if (Objects.equals(elm, it.next())) {
					indexes.add(new Integer(j));
				}
			}
			final int[] result = new int[indexes.size()];
			j = 0;
			for (final Iterator<Integer> it = indexes.iterator(); it.hasNext(); ++j) {
				final int idx = it.next().intValue();
				result[j] = idx;
			}
			return result;
		}
	}
}

