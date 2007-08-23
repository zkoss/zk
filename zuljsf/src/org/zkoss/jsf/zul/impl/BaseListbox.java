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
import java.util.Map;

import javax.faces.context.FacesContext;

import org.zkoss.jsf.zul.impl.BranchInput;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

/**
 *  The Base implementation of Listbox. 
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * @author Dennis.Chen
 * 
 */
abstract public class BaseListbox extends BranchInput {

	/**
	 * Overrride , and return null, It means that do not map value of ValueHolder to zul component. i
	 * will take case value of ValueHolder to selecteItem of listbox.
	 * 
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
			Listbox listbox = (Listbox)event.getTarget();
			Object value = event.getData();
			processSelection(listbox,value);
		}
	}

	/**
	 * set selected item of listbox by value,
	 */
	private void processSelection(Listbox listbox ,Object value){
		HashMap selections = new HashMap();
		listbox.clearSelection();
		
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
		
		ListModel model = listbox.getModel();
		int size =-1;
		Object data = null;
		
		if(model==null){
			size = listbox.getItemCount();
		}else{
			size = model.getSize();
		}
		boolean hitSel = false;
		for (int i = 0; i < size; i++) {
			data = null;
			Listitem item = listbox.getItemAtIndex(i);
			if(model==null){
				data = item.getValue();
			}else{
				data = model.getElementAt(i);
			}
			
			if (data != null && selections.get(data)!=null) {
				//if(!item.isLoaded()){
				//	listbox.renderItem(item);
				//}
				listbox.addItemToSelection(item);
				hitSel = true;
				selections.remove(data);
				if(selections.isEmpty()) break;
			}
		}
		if(hitSel){
			Events.postEvent(new SelectEvent("onSelect",listbox,listbox.getSelectedItems()));
		}
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
