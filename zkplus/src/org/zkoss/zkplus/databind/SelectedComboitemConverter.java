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

import org.zkoss.zk.ui.Component;
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
	 * Unimplemented
	 * @since 3.0.2
	 */
	public Object coerceToUi(Object val, Component comp) {
		return null;
	}

}
