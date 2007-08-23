/* BaseListbox.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 8, 2007 5:48:27 PM     2007, Created by Dennis.Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.jsf.zul.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.zkoss.jsf.zul.impl.BranchInput;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

/**
 *  The Base implementation of Tree. 
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * @author Dennis.Chen
 * 
 */
abstract public class BaseTree extends BranchInput {

	/**
	 * Overrride , and return null, It means that do not map value of ValueHolder to zul component. i
	 * will take case value of ValueHolder to selecteItem of tree 
	 * @return null
	 */
	public String getMappedAttributeName() {
		return null;
	}

	protected void afterZULComponentComposed(Component zulcomp) {
		super.afterZULComponentComposed(zulcomp);

		if (isLocalValueSet() || getValueBinding("value") != null) {
			
			zulcomp.addEventListener("onProcessZULJSFSelection",
					new ProcessSelection());
			
			//send onProcessZULJSFSelection as late as possible. 
			Events.postEvent("onProcessZULJSFSelection",zulcomp,getValue());
		}
	}
	
	private class ProcessSelection implements EventListener{
		public void onEvent(Event event) throws Exception {
			Tree tree = (Tree)event.getTarget();
			Object value = event.getData();
			processSelection(tree,value);
		}
	}

	/**
	 * set selected item of listbox by value,
	 */
	private void processSelection(Tree tree ,Object value){
		HashMap selections = new HashMap();
		tree.clearSelection();
		
		Object multi = getAttributeValue("multiple");
		boolean isMulti = (multi==null)?false:"true".equalsIgnoreCase(multi.toString())?true:false;
		if(value==null){
			return;
		}else if(isMulti){
			if(value instanceof Collection){
				Iterator iter = ((Collection)value).iterator();
				while(iter.hasNext()){
					selections.put(iter.next(),"");
				}
			}else if(value instanceof Object[]){
				Object[] s = (Object[])value;
				for(int i=0;i<s.length;i++){
					selections.put(s[i],"");
				}
			}else{
				selections.put(value,"");
			}
		}else{
			selections.put(value,"");
		}
		//go through all tree to make selection
		
		TreeModel model = tree.getModel();
		
		if(model==null){
			Treechildren tc = tree.getTreechildren();
			if(tc!=null && !selections.isEmpty()){
				dfSearchAndSelect(tree,tc.getChildren(),selections,isMulti);
			}
		}else{
			Object root = model.getRoot();
			dfSearchAndSelect(tree,root,root,model,selections,isMulti);
		}
		
	}


	/**
	 * a recursive deep first search for searh and select tree item by value in selections
	 * @param tree the tree instance
	 * @param root the root object of model
	 * @param parent the parent object to search
	 * @param model the tree model
	 * @param selections a Hashmap of selection
	 * @param isMulti true if is Mutliple selection
	 * @return true if skip search 
	 */
	private boolean dfSearchAndSelect(final Tree tree,final Object root,final Object parent,final  TreeModel model,final  HashMap selections,final  boolean isMulti) {
		if(parent ==null ){
			return false;
		}
		
		Object child = null;
		int size = model.getChildCount(parent);
		for(int i=0;i<size;i++){
			child = model.getChild(parent,i);
			if(child!=null && selections.get(child)!=null){
				//hit selection.
				int[] path = model.getPath(root,child);
				Treeitem ti = tree.renderItemByPath(path);
				if(ti!=null){
					tree.addItemToSelection(ti);
				}
				
				selections.remove(child);
				if(!isMulti || selections.isEmpty()){
					return true;
				}
			}
			
			boolean skip = dfSearchAndSelect(tree,root,child,model,selections,isMulti);
			if(skip){
				return true;
			}
		}
		return false;
		
	}

	/**
	 * a recursive deep for searh and select tree item by value in selections
	 * @param tree the tree instnace
	 * @param children a List of Treeitem
	 * @param selections a Hashmap of selection
	 * @parm isMulti true if is Mutliple selection
	 * @return return true if skip search 
	 */
	private boolean dfSearchAndSelect(final Tree tree,final List children,final HashMap selections,final boolean isMulti) {
		if(children ==null ){
			return false;
		}
		
		Object data = null;
		for(Iterator iter = children.iterator();iter.hasNext();){
			Treeitem ti = (Treeitem)iter.next();
			data = ti.getValue();
			if(data!=null && selections.get(data)!=null){
				//hit selection.
				tree.addItemToSelection(ti);
				selections.remove(data);
				if(!isMulti || selections.isEmpty()){
					return true;
				}
			}
			
			Treechildren tc = ti.getTreechildren();
			if(tc!=null && !selections.isEmpty()){
				boolean skip = dfSearchAndSelect(tree,tc.getChildren(),selections,isMulti);
				if(skip){
					return true;
				}
			}
			
		}
		return false;
	}

	/**
	 * decode parameter in request, 
	 * @param context
	 */
	protected void clientInputDecode(FacesContext context) {
		String clientId = this.getClientId(context);
		Map requestMap = context.getExternalContext().getRequestParameterMap();
		if (requestMap.containsKey(clientId)) {
			
			String[] newValue = (String[])context.getExternalContext().getRequestParameterValuesMap().get(clientId);
			
			Object multi = getAttributeValue("multiple");
			boolean isMulti = (multi==null)?false:"true".equalsIgnoreCase(multi.toString())?true:false;
			
			if(newValue!=null ){
				if(newValue.length>0){
					//JDK 1.5 BUG ? 5043241 on , incompatible types for ?: neither is a subtype of the other
					//setSubmittedValue(isMulti?newValue:newValue[0]);
					Object value;
					if(isMulti){
						value = newValue;
					}else{
						value = newValue[0];
					}
					setSubmittedValue(value);
				}
			}
		}
	}


}
