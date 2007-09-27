/* SelectedItemConverter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thur August 1 18:01:28     2007, Created by jumperchen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkforge.yuiext.zkplus.databind;

import org.zkforge.yuiext.grid.Grid;
import org.zkforge.yuiext.grid.Row;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListModel;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.TypeConverter;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Convert selected item to bean and vice versa.
 *
 * @author jumperchen
 */
public class SelectedItemConverter implements TypeConverter {
  public Object coerceToUi(Object val, Component comp) { //load
	  Grid grid = (Grid) comp;
  	if (val != null) {
  		final ListModel xmodel = grid.getModel();
  		if (xmodel instanceof BindingListModel) {
  			final BindingListModel model = (BindingListModel) xmodel;
  			int index = model.indexOf(val);
  			if (index >= 0) {
    			final Row row = (Row) grid.getRows().getChildren().get(index);
    			final int selIndex = grid.getSelectedIndex();
  				if (row != null && selIndex != index) { // bug 1647817, avoid endless-loop
    				Set rows = new HashSet();
    				rows.add(row);
    				Events.postEvent(new SelectEvent("onSelect", grid, rows));
    			}
  				return row;
  			}
  		} else if (xmodel == null) { //no model case, assume Listitem.value to be used with selectedItem
  			//iterate to find the selected item assume the value (select mold)
  			for (final Iterator it = grid.getRows().getChildren().iterator(); it.hasNext();) {
  				final Listitem li = (Listitem) it.next();
  				if (val.equals(li.getValue())) {
  					return li;
  				}
  			}
  		} else {
  			throw new UiException("model of the databind grid "+grid+" must be an instanceof of org.zkoss.zkplus.databind.BindingListModel." + xmodel);
  		}
  	}
  	return null;
  }
  
  public Object coerceToBean(Object val, Component comp) { //save
	  Grid grid = (Grid) comp;
  	if (val != null) {
  		ListModel model = grid.getModel();
  		final Row row = (Row) val;
  		//no model case, assume Row.value to be used with selectedItem
 			return model != null ? model.getElementAt(row.getParent().getChildren().indexOf(row)) : row.getValue();
  	}
 		return null;
  }
}
