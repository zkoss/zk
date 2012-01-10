/* SelectedListitemConverter.java

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
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ext.ListSelectionModel;

/**
 * Convert listbox selected listitem to bean and vice versa.
 * @author henrichen
 * @author dennis
 * @since 6.0.0
 */
public class SelectedListitemConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201108171811L;
	
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Listbox lbx = (Listbox) comp;
		final ListModel<?> model = lbx.getModel();
		//ZK-762 selection of ListModelList is not correct if binding to selectedItem
  		final ListSelectionModel smodel = (model instanceof ListSelectionModel)?(ListSelectionModel)model:null;
	  	if (val != null) {
		  	for (final Iterator<?> it = lbx.getItems().iterator(); it.hasNext();) {
		  		final Listitem li = (Listitem) it.next();
		  		final String varnm = (String) li.getAttribute(BinderImpl.VAR);
		  		Object bean = null;
		  		if (varnm != null) { //There is binding on template
		  			bean = li.getAttribute(varnm);
		  		} else if(model!=null){ //no binding
		  			bean = model.getElementAt(li.getIndex());
		  		} else{
		  			bean = li.getValue();
		  		}

		  		if (val.equals(bean)) {
		  			if(smodel!=null){
		  				final int i = li.getIndex();
		  				smodel.addSelectionInterval(i,i);
		  				return LoadPropertyBinding.LOAD_IGNORED;
		  			}
		  			return li;
		  		}
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
		  	final Listbox lbx = (Listbox) comp;
	  		final ListModel<?> model = lbx.getModel();
	  		final String varnm = (String) ((Listitem)val).getAttribute(BinderImpl.VAR);
	  		if (varnm != null) { //There is binding on template
	  			return ((Listitem)val).getAttribute(varnm);
	  		} else if(model!=null){ //no binding
	  			return model.getElementAt(((Listitem) val).getIndex());
	  		} else{
	  			return ((Listitem) val).getValue();
	  		}
	  	}
	 	return null;
	}

}
