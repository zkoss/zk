/* TreeSelectedItemsConverter.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 6:10:20 PM, Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.ext.TreeSelectableModel;

/**
 * Convert tree selected treeitem to bean and vice versa.
 * @author dennis
 * @since 6.0.0
 */
public class TreeSelectedItemsConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201108171811L;
	
	@SuppressWarnings("unchecked")
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Tree tree = (Tree) comp;
		final TreeModel<Object> model = tree.getModel();
		if(model !=null && !(model instanceof TreeSelectableModel)){
			//model has to imple TreeSelectableModel if binding to selectedItem
  			throw new UiException("model doesn't implement "+TreeSelectableModel.class);
  		}
  		final TreeSelectableModel smodel = (TreeSelectableModel)model;
  		
  		final Set<Treeitem> items = new HashSet<Treeitem>();
		Set<Object> vals = val == null ? null : (Set<Object>) Classes.coerce(HashSet.class, val);
		
		if(smodel!=null && !smodel.isSelectionEmpty()){//clear the selection first
	  		smodel.clearSelection();
		}
		
	  	if (vals != null && vals.size()>0) {
	  		if(model!=null){
	  			for(Object v:vals){
	  				int[] path = model.getPath(v);
		  			if(path!=null & smodel!=null){
		  				smodel.addSelectionPath(path);
		  			}
		  			//what if a model is not a tree selection model, there has same issue if a treeitem is not rendered yet as zk-766 event we 
	  			}
	  			return LoadPropertyBinding.LOAD_IGNORED;
	  		}
	  		//no model case
		  	//and if user want better performance, he should get the selection from model directly
			for (final Iterator<?> it = tree.getItems().iterator(); it.hasNext();) {
				final Treeitem ti = (Treeitem) it.next();
				//TODO get value form BinderImpl.VAR (Reference) for better performance
				//final String varnm = (String) ti.getAttribute(BinderImpl.VAR);
				Object bean = ti.getValue();
				if (vals.contains(bean)) {
			 		items.add(ti);
			 		vals.remove(bean);
			 		if(vals.isEmpty()) break;
			 	}
			 }
	  	}
	  	return smodel == null ? items : LoadPropertyBinding.LOAD_IGNORED;
	}

	@SuppressWarnings("unchecked")
	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		Set<Object> vals = new HashSet<Object>();
		if (val != null) {
			final Tree tree = (Tree) comp;
	  		final TreeModel<?> model = tree.getModel();
	  		if(model !=null && !(model instanceof TreeSelectableModel)){
	  			throw new UiException("model doesn't implement TreeSelectableModel");
	  		}
	  		
	  		if(model!=null){
	  			int[][] paths = ((TreeSelectableModel)model).getSelectionPaths();
	  			if(paths!=null && paths.length>0){
	  				for(int[] path:paths){
	  					vals.add(model.getChild(path));
	  				}
	  			}
	  		}else{
	  			final Set<Treeitem> items = (Set<Treeitem>)Classes.coerce(HashSet.class, val);
		  		for(Treeitem item : items){
			  		vals.add(item.getValue());
		  		}
	  		}
	  	}
	 	return vals;
	}
}
