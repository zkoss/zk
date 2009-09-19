/* SelectedComboitemConverter.java


 Purpose:
 
 Description:
 
 History:
 Jan 3, 2008 3:53:10 PM , Created by jumperchen


 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under LGPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkplus.databind;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
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
public class SelectedComboitemConverter implements TypeConverter, java.io.Serializable {
	private static final long serialVersionUID = 200808191437L;
	/**
	 * @since 3.0.2
	 */
	public Object coerceToBean(Object val, Component comp) {
		final Combobox cbbox = (Combobox) comp;
		if (Executions.getCurrent().getAttribute("zkoss.zkplus.databind.ON_SELECT"+cbbox.getUuid()) != null) {
			//bug #2140491
			//triggered by coerceToUi(), ignore this
			Executions.getCurrent().removeAttribute("zkoss.zkplus.databind.ON_SELECT"+cbbox.getUuid());
			return TypeConverter.IGNORE;
		}
	  	if (val != null) {
	  		final ListModel model = cbbox.getModel();
	  		//Bug #2010389
	  		//1. loadAll 
	  		//2. setModel (by 1.), post onInitRender. 
	  		//3. setSelectedItem (by 1.), coerceToUi, post onSelect with OLD Comboitem. 
	  		//4. onInitRender(by 2.), syncModel and reconstruct new Comboitem(s) list, fire onInitRenderLater  
	  		//** 5. save (by 3.), coreceToBean with OLD Comboitem, cannot locate the index with indexOf()
	  		//6. onInitRenderLater(by 4.), setSelectedItem, coerceToUi, post onSelect with new Comboitem
	  		
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
		  		//Bug #2010389
	  			//if combobox is going to do onInitRender (syncModel), no need to setSelectedItem
	  			if (cbbox.getAttribute("zul.Combobox.ON_INITRENDER") != null) {
	  				return TypeConverter.IGNORE;
	  			}
	  			final BindingListModel model = (BindingListModel) xmodel;
	  			int index = model.indexOf(val);
	  			if (index >= 0 && cbbox.getItemCount() > index) {
	    			final Comboitem item = (Comboitem) cbbox.getItemAtIndex(index);
	    			final int selIndex = cbbox.getSelectedIndex();
	    			
	    			//We need this to support load-when:onSelect
	  				if (item != null && selIndex != index) { // bug 1647817, avoid endless-loop
	    				Set items = new HashSet();
	    				items.add(item);
	    				//bug #2140491
	    				Executions.getCurrent().setAttribute("zkoss.zkplus.databind.ON_SELECT"+cbbox.getUuid(), Boolean.TRUE);
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
