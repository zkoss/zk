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
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ext.ListSelectionModel;

/**
 * Convert combobox selected comboitem to bean and vice versa.
 * @author henrichen
 * @author dennis
 * @since 6.0.0
 */
public class SelectedComboitemConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201108171811L;
	
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Combobox cbx = (Combobox) comp;
		final ListModel<?> model = cbx.getModel();
		//ZK-762 selection of ListModelList is not correct if binding to selectedItem
  		final ListSelectionModel smodel = (model instanceof ListSelectionModel)?(ListSelectionModel)model:null;
	  	if (val != null) {
	  		int i = 0;
		  	for (final Iterator<?> it = cbx.getItems().iterator(); it.hasNext();) {
		  		final Comboitem ci = (Comboitem) it.next();
		  		final String varnm = (String) ci.getAttribute(BinderImpl.VAR);
		  		
		  		Object bean = null;
		  		if (varnm != null) { //There is binding on template
		  			bean = ci.getAttribute(varnm);
		  		} else if(model!=null){ //no binding
		  			bean = model.getElementAt(i);
		  		} else{
		  			bean = ci.getValue();
		  		}

		  		if (val.equals(bean)) {
		  			if(smodel!=null){
		  				smodel.addSelectionInterval(i,i);
		  				return LoadPropertyBinding.LOAD_IGNORED;
		  			}
		  			return ci;
		  		}
		  		i++;
		  	}
		  	//not in the item list
	  	}
	  	
	  	if(smodel!=null){
	  		if(smodel.getMaxSelectionIndex()!=-1)
	  			smodel.clearSelection();
	  		return LoadPropertyBinding.LOAD_IGNORED;
	  	}
	  	return null;
	}

	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
	  	if (val != null) {
		  	final Combobox lbx = (Combobox) comp;
	  		final ListModel<?> model = lbx.getModel();
	  		
	  		final String varnm = (String) ((Comboitem)val).getAttribute(BinderImpl.VAR);
	  		if (varnm != null) { //There is binding on template
	  			return ((Comboitem)val).getAttribute(varnm);
	  		} else if(model!=null){ //no binding
	  			return model.getElementAt(((Comboitem) val).getIndex());
	  		} else{
	  			return ((Comboitem) val).getValue();
	  		}
	  	}
	 	return null;
	}

}
