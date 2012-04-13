/* SelectedIndexConverter.java

	Purpose:
		
	Description:
		
	History:
		Feb 3, 2012 6:10:20 PM, Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ext.Selectable;

/**
 * Convert selected index to bean and vice versa.
 * @author dennis
 * @since 6.0.0
 */
/*package*/ abstract class AbstractSelectedIndexConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201108171811L;
	
	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {

		final ListModel<?> model = getComponentModel(comp);
		//ZK-762 selection of ListModelList is not correct if binding to selectedItem
		if(model !=null && !(model instanceof Selectable)){
			//model has to imple Selectable if binding to selectedItem
  			throw new UiException("model doesn't implement Selectable");
  		}
		
	  	if (val != null) {
	  		if(model!=null){
	  			int index = ((Integer)val).intValue();
	  			if(index<0){
	  				Set<Object> sels = ((Selectable<Object>)model).getSelection();
	  				if(sels!=null && sels.size()>0)
	  		  			((Selectable<Object>)model).clearSelection();
	  			}else{
	  				((Selectable<Object>)model).addToSelection(model.getElementAt(index));
	  			}
	  			return IGNORED_VALUE;
	  		}else{
	  			return val;
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
	  	return val;
	}

	abstract protected ListModel<?> getComponentModel(Component comp);

	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
	 	return val;
	}

}
