/* BindingListModelArray.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Feb 26 17:37:25     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.zul.ListModelArray;

/**
 * <p>This is the {@link BindingListModel} as a Object array to be used with 
 * {@link org.zkoss.zul.Listbox}, {@link org.zkoss.zul.Grid},  
 * and {@link DataBinder}.
 * Modify the contents of this model as an Object array would cause the associated Listbox or Grid to change accordingly.</p> 
 * <p>Make as public class since 3.0.5</p>
 * <p>Support BindingListModelEx since 3.1</p>
 * @author Henri Chen
 * @see BindingListModel
 * @see org.zkoss.zul.ListModel
 * @see org.zkoss.zul.ListModelArray
 */
public class BindingListModelArray extends ListModelArray
implements BindingListModelExt, java.io.Serializable {
	private static final long serialVersionUID = 200808191515L;
	private boolean _distinct = true; //since 3.5; default to true
	
	/**
	 * @since 3.5.0
	 */
	public BindingListModelArray(Object[] c, boolean live, boolean distinct) {
		super(c, live);
		_distinct = distinct;
	}
	
	/**
	 * @since 3.0.5
	 */
	public BindingListModelArray(Object[] c, boolean live) {
		super(c, live);
	}
	
	public boolean isDistinct() {
		return _distinct;
	}
	
	public int[] indexesOf(Object elm) {
		if (isDistinct()) {
			final int idx = indexOf(elm);
			return idx < 0 ? new int[0] : new int[] {idx};
		} else {
			final List indexes = new LinkedList();
			for(int j = 0; j < _array.length; ++j) {
				if (Objects.equals(elm, _array[j])) {
					indexes.add(new Integer(j));
				}
			}
			final int[] result = new int[indexes.size()];
			int j = 0;
			for (final Iterator it = indexes.iterator(); it.hasNext(); ++j) {
				final int idx = ((Integer) it.next()).intValue();
				result[j] = idx;
			}
			return result;
		}
	}
}

