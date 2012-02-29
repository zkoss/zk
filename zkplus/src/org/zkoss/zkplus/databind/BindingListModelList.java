/* BindingListModelList.java

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ext.Selectable;

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
public class BindingListModelList<E> extends ListModelList<E>
implements BindingListModelExt<E>, java.io.Serializable {
	private static final long serialVersionUID = 200808191518L;
	private boolean _distinct = true; //since 3.5; default to true
	
	private Selectable<E> _selectable;
	/**
	 * @since 3.1
	 */
	@SuppressWarnings("unchecked")
	public BindingListModelList(List<E> list, boolean live, boolean distinct) {
		super(list, live);
		_distinct = distinct;
		if(list instanceof Selectable){ //since 6.0.1
			_selectable = (Selectable<E>) list;
		}
	}

	/**
	 * @since 3.0.5
	 */
	@SuppressWarnings("unchecked")
	public BindingListModelList(List<E> list, boolean live) {
		super(list, live);
		if(list instanceof Selectable){ //since 6.0.1
			_selectable = (Selectable<E>) list;
		}		
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
			for(final Iterator<E> it = _list.iterator(); it.hasNext(); ++j) {
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
	
	public Set<E> getSelection(){
		return _selectable == null ? super.getSelection() : _selectable.getSelection();
	}

	public void setSelection(Collection<? extends E> selection){
		if(_selectable == null )
			 super.setSelection(selection);
	    else 
	    	_selectable.setSelection(selection);
	}
	
	public boolean isSelected(Object obj){
		return _selectable == null ? super.isSelected(obj) : _selectable.isSelected(obj);
	}
	
	public boolean isSelectionEmpty(){
		return _selectable == null ? super.isSelectionEmpty() : _selectable.isSelectionEmpty();
	}

	public boolean addToSelection(E obj){
		return _selectable == null ? super.addToSelection(obj) : _selectable.addToSelection(obj);
	}

	public boolean removeFromSelection(Object obj){
		return _selectable == null ? super.removeFromSelection(obj) : _selectable.removeFromSelection(obj);
	}

	public void clearSelection(){
		if(_selectable == null )
			 super.clearSelection();
	    else 
	    	_selectable.clearSelection();
	}
	
	public void setMultiple(boolean multiple){
		if(_selectable == null )
			 super.setMultiple(multiple);
	    else 
	    	_selectable.setMultiple(multiple);
	}
	
	public boolean isMultiple(){
		return _selectable == null ? super.isMultiple() : _selectable.isMultiple();
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		BindingListModelList<E> clone = (BindingListModelList<E>) super.clone();
		if(clone._list instanceof Selectable){
			_selectable = (Selectable<E>) clone._list;
		}		
		return clone;
	}
}

