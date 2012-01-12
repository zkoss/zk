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
import org.zkoss.bind.impl.BinderImpl;
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
		final TreeModel<?> model = tree.getModel();

  		final TreeSelectionModel smodel = (model instanceof TreeSelectionModel)?(TreeSelectionModel)model:null;
	  	if (val != null) {
	  		//there is a BUG on tree.getItems (http://tracker.zkoss.org/browse/ZK-766),
	  		//it doesn't return all descending children if it is not open yet
	  		//and if user want better performance, he should get the selection from model directly
		  	for (final Iterator<?> it = tree.getItems().iterator(); it.hasNext();) {
		  		final Treeitem ti = (Treeitem) it.next();
		  		final String varnm = (String) ti.getAttribute(BinderImpl.VAR);
		  		Object bean = null;
		  		int path[] = null;
		  		if (varnm != null) { //There is binding on template
		  			bean = ti.getAttribute(varnm);
		  		} else if(model!=null){ //no binding
		  			bean = model.getChild(path = toPath(ti));
		  		} else{
		  			bean = ti.getValue();
		  		}

		  		if (val.equals(bean)) {
		  			if(smodel!=null){
		  				if(path==null){
		  					path = toPath(ti);
		  				}
		  				smodel.addSelectionPath(path);
		  				return LoadPropertyBinding.LOAD_IGNORED;
		  			}
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
	  		final String varnm = (String) ((Treeitem)val).getAttribute(BinderImpl.VAR);
	  		if (varnm != null) { //There is binding on template
	  			return ((Treeitem)val).getAttribute(varnm);
	  		} else if(model!=null){ //no binding
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
