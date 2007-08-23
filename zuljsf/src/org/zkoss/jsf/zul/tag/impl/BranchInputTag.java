/* BranchInputTag.java

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
import javax.faces.event.ValueChangeEvent;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.zkoss.jsf.zul.impl.BranchInput;


/**
 * The skeletal class for implementing the generic JSF tags.
 * 
 * @author Dennis.Chen
 */
abstract public class BranchInputTag extends BranchOutputTag implements DynamicAttributes{

	/**
	 * constructor.
	 * @param typeName a type name with will be concatenate a {@link #COMP_TYPE_PREFIX},return at {@link #getComponentType}, 
	 */
	protected BranchInputTag(String typeName) {
		super(typeName);
	}

	/**
	 * Override method, Set properties of ZULJSF Component
	 */
	protected void setProperties(UIComponent comp) {
		super.setProperties(comp);
		
		//take care immedate,require,validator,valuechange listener
		if(JSF_CORD_NS.equals(_specialNS.get("immediate"))){
			Object obj = _dynamicAttrMap.get("immediate");
			if(obj!=null){
				if(!(obj instanceof String)) throw new RuntimeException("attribute 'immediate' must be String");
				if (isValueReference((String)obj)) {
	                ValueBinding vb = getFacesContext().getApplication().createValueBinding((String)obj);
	                ((BranchInput)comp).setValueBinding("immediate", vb);
	            } else {
	                boolean immediate = new Boolean((String)obj).booleanValue();
	                ((BranchInput)comp).setImmediate(immediate);
	            }
			}
			
		}
		
		if (JSF_CORD_NS.equals(_specialNS.get("required"))) {
			Object obj = _dynamicAttrMap.get("required");
			if(obj!=null){
				if(!(obj instanceof String)) throw new RuntimeException("attribute 'required' must be String");
				if (isValueReference((String)obj)) {
	                ValueBinding vb = getFacesContext().getApplication().createValueBinding((String)obj);
	                ((BranchInput)comp).setValueBinding("required", vb);
	            } else {
	                boolean required = new Boolean((String)obj).booleanValue();
	                ((BranchInput)comp).setRequired(required);
	            }
			}
        }
		
		if (JSF_CORD_NS.equals(_specialNS.get("validator"))) {
			Object obj = _dynamicAttrMap.get("validator");
			if(obj!=null){
				if(!(obj instanceof String)) throw new RuntimeException("attribute 'validator' must be String");
				if (isValueReference((String)obj)) {
	                Class args[] = { FacesContext.class, UIComponent.class, Object.class };
	                MethodBinding vb = FacesContext.getCurrentInstance().getApplication().createMethodBinding((String)obj, args);
	                ((BranchInput)comp).setValidator(vb);
	            } else {
	            	throw new javax.faces.FacesException("Validator must be a MethodBinding Expression:"+obj);
	            }
			}
        }
		
		if (JSF_CORD_NS.equals(_specialNS.get("valueChangeListener"))) {
			Object obj = _dynamicAttrMap.get("valueChangeListener");
			if(obj!=null){
				if (isValueReference((String)obj)) {
					if(!(obj instanceof String)) throw new RuntimeException("attribute 'valueChangeListener' must be String");
					Class args[] = { ValueChangeEvent.class };
	                MethodBinding vb = FacesContext.getCurrentInstance().getApplication().createMethodBinding((String)obj, args);
	                ((BranchInput)comp).setValueChangeListener(vb);
	            } else {
	            	throw new javax.faces.FacesException("ValueChangeListener must be a MethodBinding Expression:"+obj);
	            }
			}
        }
		
		
	}
	
}
