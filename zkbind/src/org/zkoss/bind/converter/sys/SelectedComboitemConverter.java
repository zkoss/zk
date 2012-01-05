/* SelectedComboitemConverter.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 6:10:20 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import java.util.Iterator;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Comboitem;

/**
 * Convert combobox selected comboitem to bean and vice versa.
 * @author henrichen
 *
 */
public class SelectedComboitemConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201108171811L;
	
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Combobox cbx = (Combobox) comp;
	  	if (val != null) {
	  		final ListModel<?> model = cbx.getModel();
	  		if (model != null) {
		  		if (cbx.getAttribute(BinderImpl.RENDERER_INSTALLED) != null) { //There is binding on template
		  			//TODO might be done with dependency tracker, bean -> listitem (implicit binding)
		  			//iterate to find the selected item
		  			for (final Iterator<?> it = cbx.getItems().iterator(); it.hasNext();) {
		  				final Comboitem li = (Comboitem) it.next();
		  				final String varnm = (String) li.getAttribute(BinderImpl.VAR);
		  				final Object bean = li.getAttribute(varnm);
		  				if (val.equals(bean)) {
		  					return li;
		  				}
		  			}
		  			//not in the item list
		  			return null;
		  		}
	  		}
	  		//no model or no binding, assume Comboitem.value to be used with selectedItem
  			//iterate to find the selected item
  			for (final Iterator<?> it = cbx.getItems().iterator(); it.hasNext();) {
  				final Comboitem li = (Comboitem) it.next();
  				if (val.equals(li.getValue())) {
  					return li;
  				}
  			}
	  	}
	  	return null;
	}

	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
	  	if (val != null) {
		  	final Combobox lbx = (Combobox) comp;
	  		final ListModel<?> model = lbx.getModel();
	  		if (model != null) {
		  		final String varnm = (String) lbx.getAttribute("$VAR$");
		  		if (varnm != null) { //There is binding on template
		  			return ((Comboitem)val).getAttribute(varnm);
		  		} else { //no binding
		  			return model.getElementAt(((Comboitem) val).getIndex());
		  		}
	  		} else { //no model case, assume Comboitem.value to be used with selectedItem
	  			return ((Comboitem) val).getValue();
	  		}
	  	}
	 	return null;
	}

}
