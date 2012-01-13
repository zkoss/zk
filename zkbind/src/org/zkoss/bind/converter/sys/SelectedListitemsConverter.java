/* SelectedListitemConverter.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 6:10:20 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ext.ListSelectionModel;

/**
 * Convert listbox selected listitems to bean and vice versa.
 * @author dennis
 * @since 6.0.0
 */
public class SelectedListitemsConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201108171811L;
	
	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Listbox lbx = (Listbox) comp;
		final ListModel<?> model = lbx.getModel();
  		final ListSelectionModel smodel = (model instanceof ListSelectionModel)?(ListSelectionModel)model:null;
  		final Set<Listitem> items = new LinkedHashSet<Listitem>();
		Set<Object> vals = val == null ? null : (Set<Object>) Classes.coerce(LinkedHashSet.class, val);
		
		if(smodel!=null && smodel.getMaxSelectionIndex()!=-1){//clear the selection first
	  		smodel.clearSelection();
		}
		
	  	if (vals != null && vals.size()>0) {
		  	for (final Iterator<?> it = lbx.getItems().iterator(); it.hasNext();) {
		  		final Listitem li = (Listitem) it.next();
		  		Object bean = null;
		  		if(model!=null){ //no binding
		  			bean = model.getElementAt(li.getIndex());
		  		} else{
		  			bean = li.getValue();
		  		}

		  		if (vals.contains(bean)) {
		  			if(smodel!=null){
		  				final int i = li.getIndex();
		  				smodel.addSelectionInterval(i,i);
		  			}
		  			items.add(li);
		  		}
		  	}			
	  	}
	  	return smodel == null ? items : LoadPropertyBinding.LOAD_IGNORED;
	}

	@SuppressWarnings("unchecked")
	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		Set<Object> vals = new LinkedHashSet<Object>();//the order
		if (val != null) {
			final Listbox lbx = (Listbox) comp;
	  		final ListModel<?> model = lbx.getModel();
	  		final Set<Listitem> items = (Set<Listitem>)Classes.coerce(LinkedHashSet.class, val);
	  		for(Listitem item : items){
		  		if(model != null){ //from model value
		  			vals.add(model.getElementAt(item.getIndex()));
		  		} else { //no binding
		  			vals.add(item.getValue());
		  		}
	  		}
	  		return vals;
	  	}
	 	return vals;
	}

}
