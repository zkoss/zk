/* SelectedListitemConverter.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 6:10:20 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.ext.Selectable;

/**
 * Convert listbox selected listitem to bean and vice versa.
 * @author henrichen
 * @author dennis
 * @since 6.0.0
 */
public class SelectboxSelectedItemConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201108171811L;
	
	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Selectbox sbox = (Selectbox) comp;
		final ListModel<?> model = sbox.getModel();
		if(model==null){
			throw new UiException("no model in selectbox");
		}
		if(!(model instanceof Selectable)){
  			throw new UiException("model doesn't implement Selectable");
  		}
		
	  	if (val != null) {
	  		((Selectable<Object>)model).addToSelection(val);
	  		return IGNORED_VALUE;
	  	}
	  	
	  	Set<Object> sels = ((Selectable<Object>)model).getSelection();
	  	if(sels!=null && sels.size()>0)
	  		((Selectable<Object>)model).clearSelection();
	  	return IGNORED_VALUE;
	}

	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		//since there is always a model, we get the selected by item by model directly
	  	if (val != null) {
	  		Selectbox sbox = (Selectbox) comp;
			final ListModel<?> model = sbox.getModel();
			if(model==null){
				throw new UiException("no model in selectbox");
			}
			if(!(model instanceof Selectable)){
	  			throw new UiException("model doesn't implement Selectable");
	  		}
			Set<?> selection = ((Selectable<?>)model).getSelection();
  			if(selection==null || selection.size()==0) return null;
  			return selection.iterator().next();
	  	}
	 	return null;
	}

}
