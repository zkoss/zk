/* ListboxSelectedItemConverter.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 6:10:20 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import java.util.Iterator;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ext.Selectable;

/**
 * Convert listbox selected listitem to bean and vice versa.
 * @author henrichen
 * @author dennis
 * @since 6.0.0
 */
public class ListboxSelectedItemConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201108171811L;
	
	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Listbox lbx = (Listbox) comp;
		final ListModel<?> model = lbx.getModel();
		//ZK-762 selection of ListModelList is not correct if binding to selectedItem
		if(model !=null && !(model instanceof Selectable)){
			//model has to imple Selectable if binding to selectedItem
  			throw new UiException("model doesn't implement Selectable");
  		}
		
	  	if (val != null) {
	  		if(model!=null){
	  			((Selectable<Object>)model).addToSelection(val);
	  			return IGNORED_VALUE;
	  		}else{
	  			//no model case
			  	for (final Iterator<?> it = lbx.getItems().iterator(); it.hasNext();) {
			  		final Listitem li = (Listitem) it.next();
			  		Object bean = li.getValue();
			  		if (val.equals(bean)) {
			  			return li;
			  		}
			  	}
	  		}
		  	//not in the item list
	  	}
	  	
	  //nothing matched, clean the old selection
	  	if(model!=null){
	  		Set<Object> sels = ((Selectable<Object>)model).getSelection();
	  		if(sels!=null && sels.size()>0)
	  			((Selectable<Object>)model).clearSelection();
	  		return IGNORED_VALUE;
	  	}
	  	return null;
	}

	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
	  	if (val != null) {
		  	final Listbox lbx = (Listbox) comp;
	  		final ListModel<?> model = lbx.getModel();
	  		if(model !=null && !(model instanceof Selectable)){
	  			throw new UiException("model doesn't implement Selectable");
	  		}
	  		if(model!=null){
	  			Set<?> selection = ((Selectable<?>)model).getSelection();
	  			if(selection==null || selection.size()==0) return null;
	  			return selection.iterator().next();
	  		} else{//no model
	  			return ((Listitem) val).getValue();
	  		}
	  	}
	 	return null;
	}

}
