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
public class BindingListModelListModel implements BindingListModel, Selectable, java.io.Serializable {
	
	protected ListModel _model;
	
	public BindingListModelListModel(ListModel model) {
		_model = model;
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

	public Object getElementAt(int index) {
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
	
	public ListModel getInnerModel(){
		return _model;
	}
	private static class EmptySelectable implements Selectable, java.io.Serializable {

		@Override
		public Set getSelection() {
			return null;
		}
		@Override
		public void setSelection(Collection selection) {
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
		public void addToSelection(Object obj) {
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
	private <E> Selectable<E> getSelectModel() {
		if (_model instanceof Selectable)
			return (Selectable) _model;
		return new EmptySelectable();
	}
	@Override
	public Set getSelection() {
		return getSelectModel().getSelection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelection(Collection selection) {
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
	public void addToSelection(Object obj) {
		getSelectModel().addToSelection(obj);
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
}
