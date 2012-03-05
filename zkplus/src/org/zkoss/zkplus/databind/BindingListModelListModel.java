/* BindingListModelListModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 13, 2011 15:06:25 AM, Created by peterkuo
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/


package org.zkoss.zkplus.databind;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Selectable;

/**
 * <p>This is the {@link BindingListModel} as a {@link ListModel} to be used with 
 * {@link org.zkoss.zul.Listbox}, {@link org.zkoss.zul.Grid},  
 * and {@link DataBinder}.
 * Add or remove the contents of this model as a ListModel would cause the associated Listbox or Grid to change accordingly.</p> 
 *
 * @author peterkuo
 * @since 5.0.8
 */
public class BindingListModelListModel<E> implements BindingListModel<E>, Selectable<E>, java.io.Serializable ,BindingListModelExt<E>{
	
	private static final long serialVersionUID = -4454049848612772393L;
	
	protected ListModel<E> _model;
	private Selectable<E> _selectable;
	private boolean distinct = true;
	
	public BindingListModelListModel(ListModel<E> model) {
		_model = model;
	}
	
	public BindingListModelListModel(ListModel<E> model,boolean distinct) {
		_model = model;
		this.distinct = distinct;
		if(model instanceof Set || model instanceof Map){
			distinct = true;
		}
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

	public E getElementAt(int index) {
		return _model.getElementAt(index);
	}

	public int getSize() {
		return _model.getSize();
	}

	public void addListDataListener(ListDataListener l) {
		_model.addListDataListener(l);		
	}

	public void removeListDataListener(ListDataListener l) {
		_model.removeListDataListener(l);
	}
	
	public ListModel<E> getInnerModel(){
		return _model;
	}
	private static class EmptySelectable<E> implements Selectable<E>, java.io.Serializable {
		private static final long serialVersionUID = 7942486785062723611L;

		@Override
		public Set<E> getSelection() {
			return null;
		}
		@Override
		public void setSelection(Collection<? extends E> selection) {
		}

		@Override
		public boolean isSelected(Object obj) {
			return false;
		}

		@Override
		public boolean isSelectionEmpty() {
			return false;
		}

		@Override
		public boolean addToSelection(E obj) {
			return false;
		}

		@Override
		public boolean removeFromSelection(Object obj) {
			return false;
		}

		@Override
		public void clearSelection() {
		}

		@Override
		public void setMultiple(boolean multiple) {
		}

		@Override
		public boolean isMultiple() {
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	private Selectable<E> getSelectModel() {
		if (_model instanceof Selectable<?>)
			return (Selectable<E>) _model;
		
		if(_selectable == null){
			_selectable = new EmptySelectable<E>();
		}
		
		return _selectable;
	}
	@Override
	public Set<E> getSelection() {
		return getSelectModel().getSelection();
	}

	@Override
	public void setSelection(Collection<? extends E> selection) {
		getSelectModel().setSelection(selection);
	}

	@Override
	public boolean isSelected(Object obj) {
		return getSelectModel().isSelected(obj);
	}

	@Override
	public boolean isSelectionEmpty() {
		return getSelectModel().isSelectionEmpty();
	}

	@Override
	public boolean addToSelection(E obj) {
		return getSelectModel().addToSelection(obj);
	}

	@Override
	public boolean removeFromSelection(Object obj) {
		return getSelectModel().removeFromSelection(obj);
	}

	@Override
	public void clearSelection() {
		getSelectModel().clearSelection();
	}

	@Override
	public void setMultiple(boolean multiple) {
		getSelectModel().setMultiple(multiple);
	}

	@Override
	public boolean isMultiple() {
		return getSelectModel().isMultiple();
	}
	
	//--BindingListModelExt--//
	@Override
	public int[] indexesOf(Object elm) {
		if (isDistinct()) {
			final int idx = indexOf(elm);
			return idx < 0 ? new int[0] : new int[] {idx}; 
		} else {
			final List<Integer> indexes = new LinkedList<Integer>();
	
			for(int ind = getSize(); ind-- > 0;) {
				//final Iterator<E> it = _list.iterator(); it.hasNext(); ++j
				if (Objects.equals(elm, getElementAt(ind))) {
					indexes.add(Integer.valueOf(ind));
				}
			}
			
			int j = 0;
			final int[] result = new int[indexes.size()];
			j = 0;
			for (final Iterator<Integer> it = indexes.iterator(); it.hasNext(); ++j) {
				final int idx = it.next().intValue();
				result[j] = idx;
			}
			return result;
		}
	}

	@Override
	public boolean isDistinct() {
		return distinct;
	}
}
