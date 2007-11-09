/* CommandTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8 10:00:10     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.tag.impl;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import org.zkoss.jsf.zul.impl.BaseCommand;
import org.zkoss.jsf.zul.impl.BranchInput;

/**
 * The skeletal class for implementing the generic JSF tags.
 * @author Dennis.Chen
 */
abstract public class CommandTag extends BranchTag {
	
	 private static final Class[] ACTION_LISTENER_ARGS = {ActionEvent.class};
	 
	/**
	 * constructor.
	 * @param typeName a type name with will be concatenate a {@link #COMP_TYPE_PREFIX},return at {@link #getComponentType}, 
	 */
	protected CommandTag(String typeName) {
		super(typeName);
	}

	/**
	 * Override method, Set properties of ZULJSF Component
	 */
	protected void setProperties(UIComponent comp) {
		super.setProperties(comp);
		
		//take care action
		
			Object obj = _jsfcoreAttrMap.get("action");
			if(obj!=null){
				if(!(obj instanceof String)) throw new RuntimeException("attribute 'action' must be String");
				if (isValueReference((String)obj)) {
	                MethodBinding mb = FacesContext.getCurrentInstance().getApplication().createMethodBinding((String)obj, null);
	                ((BaseCommand)comp).setAction(mb);
	            } else {
	            	throw new javax.faces.FacesException("action must be a MethodBinding Expression:"+obj);
	            }
			}
        
			obj = _jsfcoreAttrMap.get("actionListener");
			if(obj!=null){
				if(!(obj instanceof String)) throw new RuntimeException("attribute 'actionListener' must be String");
				if (isValueReference((String)obj)) {
	                MethodBinding mb = FacesContext.getCurrentInstance().getApplication().createMethodBinding((String)obj, ACTION_LISTENER_ARGS);
	                ((BaseCommand)comp).setActionListener(mb);
	            } else {
	            	throw new javax.faces.FacesException("actionListener must be a MethodBinding Expression:"+obj);
	            }
			}
		
			obj = _jsfcoreAttrMap.get("immediate");
			if(obj!=null){
				if ((obj instanceof String) && isValueReference((String)obj)) {
	                ValueBinding vb = getFacesContext().getApplication().createValueBinding((String)obj);
	                ((BaseCommand)comp).setValueBinding("immediate", vb);
	            } else if(obj instanceof String){
	                boolean immediate = new Boolean((String)obj).booleanValue();
	                ((BaseCommand)comp).setImmediate(immediate);
	            } else if (obj instanceof Boolean) {
					((BaseCommand) comp).setImmediate(((Boolean) obj).booleanValue());
				} else{
					throw new RuntimeException("attribute 'immediate' , unsupported type:"+obj.getClass());
				}
			}
			
	}
}
