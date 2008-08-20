/* SelectedItemConverter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 12 15:43:28     2006, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
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
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

/**
 * Convert selected item to bean and vice versa.
 *
 * @author Henri
 */
public class SelectedItemConverter implements TypeConverter, java.io.Serializable {
	private static final long serialVersionUID = 200808191439L;
	public Object coerceToUi(Object val, Component comp) { //load
		Listbox lbx = (Listbox) comp;
	  	if (val != null) {
	  		final ListModel xmodel = lbx.getModel();
	  		if (xmodel instanceof BindingListModel) {
	  			final BindingListModel model = (BindingListModel) xmodel;
	  			int index = model.indexOf(val);
	  			if (index >= 0) {
	    			final Listitem item = (Listitem) lbx.getItemAtIndex(index);
	    			
	    			final int selIndex = lbx.getSelectedIndex();
	    			
					//We need this to support load-when:onSelect when first load 
					//the page in (so it is called only once).
	  				if (item != null && selIndex != index) { // bug 1647817, avoid endless-loop
	    				Set items = new HashSet();
	    				items.add(item);
	    				lbx.setAttribute("zkoss.zkplus.databind.ON_SELECT", Boolean.TRUE);
	    				Events.postEvent(new SelectEvent("onSelect", lbx, items, item));
	    			}    			
	  				return item;
	  			}
	  		} else if (xmodel == null) { //no model case, assume Listitem.value to be used with selectedItem
	  			//iterate to find the selected item assume the value (select mold)
	  			for (final Iterator it = lbx.getItems().iterator(); it.hasNext();) {
	  				final Listitem li = (Listitem) it.next();
	  				if (val.equals(li.getValue())) {
	  					return li;
	  				}
	  			}
	  		} else {
	  			throw new UiException("model of the databind listbox "+lbx+" must be an instanceof of org.zkoss.zkplus.databind.BindingListModel." + xmodel);
	  		}
	  	}
	  	return null;
	}
  
	public Object coerceToBean(Object val, Component comp) { //save
	  	Listbox lbx = (Listbox) comp;
		if (lbx.getAttribute("zkoss.zkplus.databind.ON_SELECT") != null) {
			//triggered by coerceToUi(), ignore this
			lbx.removeAttribute("zkoss.zkplus.databind.ON_SELECT");
			return TypeConverter.IGNORE;
		}
	  	if (val != null) {
	  		ListModel model = lbx.getModel();
	  		//no model case, assume Listitem.value to be used with selectedItem
	 			return model != null ? model.getElementAt(((Listitem) val).getIndex()) : ((Listitem) val).getValue();
	  	}
	 		return null;
	}
}
