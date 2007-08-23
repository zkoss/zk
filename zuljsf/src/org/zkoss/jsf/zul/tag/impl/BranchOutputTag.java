/* LeafOutputTag.java

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
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.zkoss.jsf.zul.impl.BranchOutput;
import org.zkoss.jsf.zul.impl.ValueHolderSupport;


/**
 * The skeletal class for implementing the generic JSF tags.
 * 
 * @author Dennis.Chen
 */
abstract public class BranchOutputTag extends LeafTag implements DynamicAttributes{
	/**
	 * constructor.
	 * @param typeName a type name with will be concatenate a {@link #COMP_TYPE_PREFIX},return at {@link #getComponentType}, 
	 */
	protected BranchOutputTag(String typeName) {
		super(typeName);
	}
	
	/**
	 * Override method, Set properties of ZULJSF Component
	 */
	protected void setProperties(UIComponent comp) {
		super.setProperties(comp);
		
		//block the attribute of value
		if(comp instanceof ValueHolderSupport){
			String attName = ((ValueHolderSupport)comp).getMappedAttributeName();
			if(attName!=null && _dynamicAttrMap.get(attName)!=null && !JSF_CORD_NS.equals(_specialNS.get(attName))){
				throw new IllegalArgumentException("for JSF feature of ValueHolder, use perfix of <"+JSF_CORD_NS+">:value instead of "+attName+" in component:"+this.getClass());
			}
		}
		
		//take value & converter.
		if(JSF_CORD_NS.equals(_specialNS.get("value"))){
			Object obj = _dynamicAttrMap.get("value");
			
			if(obj!=null){
				if(!(obj instanceof String)) throw new RuntimeException("attribute 'value' must be String");
				if (isValueReference((String)obj)) {
	                ValueBinding vb = getFacesContext().getApplication().createValueBinding((String)obj);
	                ((BranchOutput)comp).setValueBinding("value", vb);
	            } else {
	            	((BranchOutput)comp).setValue(obj);
	            }
			}
		}
		
		
		if (JSF_CORD_NS.equals(_specialNS.get("converter"))) {
			Object obj = _dynamicAttrMap.get("converter");
			
			if(obj!=null){
				if(!(obj instanceof String)) throw new RuntimeException("attribute 'converter' must be String");
	            if (isValueReference((String)obj)) {
	            	ValueBinding vb = getFacesContext().getApplication().createValueBinding((String)obj);
	                ((BranchOutput)comp).setValueBinding("converter", vb);
	            } else {
	                Converter _converter = FacesContext.getCurrentInstance().getApplication().createConverter((String)obj);
	                ((BranchOutput)comp).setConverter(_converter);
	            }
			}
        }
		
		
	}
	
}
