/* SelectedComboitemConverter.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jan 3, 2008 3:53:10 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkplus.databind;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModel;

/**
 * Convert the selected item of combobox to bean.
 * @author jumperchen
 * @since 3.0.2
 */
public class SelectedComboitemConverter implements TypeConverter {
	/**
	 * @since 3.0.2
	 */
	public Object coerceToBean(Object val, Component comp) {
		Combobox cbbox = (Combobox) comp;
	  	if (val != null) {
	  		ListModel model = cbbox.getModel();
	  		//no model case, assume Comboitem.value to be used with selectedItem
	 		return model != null ? model.getElementAt(cbbox.getItems().indexOf(val)) : ((Comboitem) val).getValue();
	  	}
	 	return null;
	}
	
	/**
	 * @since 3.0.2
	 */
	public Object coerceToUi(Object val, Component comp) {
		final Combobox cbbox = (Combobox) comp;
	  	if (val != null) {
	  		final ListModel xmodel = cbbox.getModel();
	  		if (xmodel instanceof BindingListModel) {
	  			final BindingListModel model = (BindingListModel) xmodel;
	  			int index = model.indexOf(val);
	  			if (index >= 0) {
	    			final Comboitem item = (Comboitem) cbbox.getItemAtIndex(index);
	    			final int selIndex = cbbox.getSelectedIndex();
	    			
	    			//We need this to support load-when:onSelect when first load 
					//the page in (so it is called only once).
	  				if (item != null && selIndex != index) { // bug 1647817, avoid endless-loop
	    				Set items = new HashSet();
	    				items.add(item);
	    				Events.postEvent(new SelectEvent("onSelect", cbbox, items, item));
	    			}		
	  				return item;
	  			}
	  		} else if (xmodel == null) { //no model case, assume Comboitem.value to be used with selectedItem
	  			//iterate to find the selected item assume the value (select mold)
	  			for (final Iterator it = cbbox.getItems().iterator(); it.hasNext();) {
	  				final Comboitem li = (Comboitem) it.next();
	  				if (val.equals(li.getValue())) {
	  					return li;
	  				}
	  			}
	  		} else {
	  			throw new UiException("model of the databind combobox "+cbbox+" must be an instanceof of org.zkoss.zkplus.databind.BindingListModel." + xmodel);
	  		}
	  	}
	  	return null;
	}

}
