/* SelectedTreeitemConverter.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 6:10:20 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import java.util.ArrayList;
import java.util.Iterator;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.ext.TreeSelectionModel;

/**
 * Convert tree selected treeitem to bean and vice versa.
 * @author henrichen
 * @author dennis
 * @since 6.0.0
 */
public class SelectedTreeitemConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 201109261823L;
	
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		Tree tree = (Tree) comp;
		final TreeModel<Object> model = tree.getModel();

  		final TreeSelectionModel smodel = (model instanceof TreeSelectionModel)?(TreeSelectionModel)model:null;
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
		  	//there is a issue on tree.getItems (http://tracker.zkoss.org/browse/ZK-766),
		  	//it doesn't return all descending children if it is not open yet
		  	//and if user want better performance, he should get the selection from model directly
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
	  		if(model!=null){ //no binding
	  			return model.getChild(toPath((Treeitem) val));
	  		} else{
	  			return ((Treeitem) val).getValue();
	  		}
	  	}
	 	return null;
	}

	private int[] toPath(Treeitem item) {
		ArrayList<Integer> path = new ArrayList<Integer>();
		while(item!=null){
			path.add(0,item.getIndex());
			item = item.getParentItem();
		}
		int[] p = new int[path.size()];
		for(int i=0;i<p.length;i++){
			p[i] = path.get(i).intValue();
		}
		return p;
	}

}
