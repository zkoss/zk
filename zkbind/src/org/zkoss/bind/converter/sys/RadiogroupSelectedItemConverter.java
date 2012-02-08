/* RadiogroupSelectedItemConverter

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 12 11:05:43     2007, Created by Henri
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.bind.converter.sys;

import java.util.Iterator;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.ext.Selectable;

/**
 * Convert Radiogroup selected item to radio value and vice versa.
 *
 * @author Dennis
 * @since 6.0.0
 */
public class RadiogroupSelectedItemConverter implements Converter, java.io.Serializable {
  	private static final long serialVersionUID = 200808191534L;
  	
  	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Radiogroup radiogroup = (Radiogroup) comp;
		final ListModel<?> model = radiogroup.getModel();
		//ZK-762 selection of ListModelList is not correct if binding to selectedItem
		if(model !=null && !(model instanceof Selectable)){
			//model has to imple Selectable if binding to selectedItem
  			throw new UiException("model doesn't implement Selectable");
  		}
		
	  	if (val != null) {
	  		if(model!=null){
	  			((Selectable<Object>)model).addToSelection(val);
	  			return LoadPropertyBinding.LOAD_IGNORED;
	  		}else{
	  			//no model case
			  	for (final Iterator<?> it = radiogroup.getItems().iterator(); it.hasNext();) {
			  		final Radio radio = (Radio) it.next();			  		
			  		String value = radio.getValue();
			  		if (val.equals(value)) {
			  			return radio;
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
	  		return LoadPropertyBinding.LOAD_IGNORED;
	  	}
	  	return null;
	}

	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
	  	if (val != null) {
	  		final ListModel<?> model = ((Radio)val).getRadiogroup().getModel();
	  		
	  		if(model !=null && !(model instanceof Selectable)){
	  			throw new UiException("model doesn't implement Selectable");
	  		}
	  		if(model!=null){
	  			Set<?> selection = ((Selectable<?>)model).getSelection();
	  			if(selection==null || selection.size()==0) return null;
	  			return selection.iterator().next();
	  		} else{//no model
	  			return ((Radio) val).getValue();
	  		}
	  	}
	 	return null;
	}
}
