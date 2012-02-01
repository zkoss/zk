/* SelectedComboitemConverter.java

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
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ext.Selectable;


/**
 * Convert combobox selected comboitem to bean and vice versa.
 * @author henrichen
 * @author dennis
 * @since 6.0.0
 */
public class SelectedComboitemConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201108171811L;
	
	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Combobox cbx = (Combobox) comp;
		final ListModel<?> model = cbx.getModel();
		//ZK-762 selection of ListModelList is not correct if binding to selectedItem
  		if(model !=null && !(model instanceof Selectable)){
  			//model has to imple Selectable if binding to selectedItem
  			throw new UiException("model doesn't implement Selectable");
  		}
  		
  		//Notice, clear selection will cause combobox fire onAfterRender, and then reload selectedItem , 
  		//it cause infinity loop
  		
	  	if (val != null) {
	  		if(model!=null){
	  			((Selectable<Object>)model).addToSelection(val);
	  			return LoadPropertyBinding.LOAD_IGNORED;
	  		}else{
	  			//no model case
		  		int i = 0;
			  	for (final Iterator<?> it = cbx.getItems().iterator(); it.hasNext();) {
			  		final Comboitem ci = (Comboitem) it.next();
			  		
			  		Object bean = ci.getValue();
	
			  		if (val.equals(bean)) {
			  			return ci;
			  		}
			  		i++;
			  	}
	  		}
		  	//not in the item list
	  	}
	  	
	  	//nothing matched, clean the old selection
	  	if(model!=null){
	  		Set<Object> sels = ((Selectable<Object>)model).getSelection();
	  		if(sels!=null && sels.size()>0)
	  			((Selectable<Object>)model).clearSelection();
	  		return LoadPropertyBinding.LOAD_IGNORED;
	  	}
	  	return null;
	}

	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
	  	if (val != null) {
		  	final Combobox lbx = (Combobox) comp;
	  		final ListModel<?> model = lbx.getModel();
	  		if(model!=null){ //no binding
	  			return model.getElementAt(((Comboitem) val).getIndex());
	  		} else{
	  			return ((Comboitem) val).getValue();
	  		}
	  	}
	 	return null;
	}

}
