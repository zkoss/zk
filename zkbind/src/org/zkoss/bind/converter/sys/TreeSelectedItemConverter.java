/* TreeSelectedItemConverter.java

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
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.ext.TreeSelectableModel;

/**
 * Convert tree selected treeitem to bean and vice versa.
 * @author henrichen
 * @author dennis
 * @since 6.0.0
 */
public class TreeSelectedItemConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201109261823L;
	
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Tree tree = (Tree) comp;
		final TreeModel<Object> model = tree.getModel();

  		if(model !=null && !(model instanceof TreeSelectableModel)){
			//model has to imple TreeSelectableModel if binding to selectedItem
  			throw new UiException("model doesn't implement TreeSelectableModel");
  		}
  		final TreeSelectableModel smodel = (TreeSelectableModel)model;
		if(smodel!=null && !smodel.isSelectionEmpty()){//clear the selection first
	  		smodel.clearSelection();
		}
		
	  	if (val != null) {
	  		if(model!=null){
	  			int[] path = model.getPath(val);
	  			if(path!=null && smodel!=null){
	  				smodel.addSelectionPath(path);
	  				return LoadPropertyBinding.LOAD_IGNORED;
	  			}
	  			//what if a model is not a tree selection model, there has same issue if a treeitem is not rendered yet as zk-766
	  		}	  		
	  		//no model case
		  	//if user want better performance, he should get the selection from model directly
			for (final Iterator<?> it = tree.getItems().iterator(); it.hasNext();) {
				final Treeitem ti = (Treeitem) it.next();
				Object bean = ti.getValue();
				if (val.equals(bean)) {
					return ti;
				}
			}
	  		
		  	//not in the item list
	  	}
	  	
	  	if(smodel!=null){
	  		if(smodel.getSelectionCount()>0)
	  			smodel.clearSelection();
	  		return LoadPropertyBinding.LOAD_IGNORED;
	  	}
	  	return null;
	}

	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		if (val != null) {
		  	final Tree tree = (Tree) comp;
	  		final TreeModel<?> model = tree.getModel();
	  		if(model !=null && !(model instanceof TreeSelectableModel)){
	  			throw new UiException("model doesn't implement TreeSelectableModel");
	  		}
	  		if(model!=null){
	  			int[] path = ((TreeSelectableModel)model).getSelectionPath();
	  			if(path==null) return null;
	  			return model.getChild(path);
	  		} else{
	  			return ((Treeitem) val).getValue();
	  		}
	  	}
	 	return null;
	}
}
