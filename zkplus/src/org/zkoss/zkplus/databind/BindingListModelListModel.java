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

import org.zkoss.lang.Objects;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataListener;

/**
 * <p>This is the {@link BindingListModel} as a {@link ListModel} to be used with 
 * {@link org.zkoss.zul.Listbox}, {@link org.zkoss.zul.Grid},  
 * and {@link DataBinder}.
 * Add or remove the contents of this model as a ListModel would cause the associated Listbox or Grid to change accordingly.</p> 
 *
 * @author peterkuo
 * @since 5.0.8
 */
public class BindingListModelListModel implements BindingListModel, java.io.Serializable {
	
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
}
