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
abstract public class BranchOutputTag extends LeafTag implements DynamicAttributes {
	/**
	 * constructor.
	 * 
	 * @param typeName
	 *            a type name with will be concatenate a
	 *            {@link #COMP_TYPE_PREFIX},return at {@link #getComponentType},
	 */
	protected BranchOutputTag(String typeName) {
		super(typeName);
	}

	/**
	 * Override method, Set properties of ZULJSF Component
	 */
	protected void setProperties(UIComponent comp) {
		super.setProperties(comp);

		// block the attribute of value
		if (comp instanceof ValueHolderSupport) {
			String attName = ((ValueHolderSupport) comp).getMappedAttributeName();
			if (attName != null) {
				Object value = _dynamicAttrMap.get(attName);
				boolean hitann = false;
				if (value != null && value instanceof String) {
					int len = ((String) value).length();
					if (len >= 3 && ((String) value).charAt(0) == '@' && ((String) value).charAt(1) == '{'
							&& ((String) value).charAt(len - 1) == '}') { // annotation
						// do nothing when annotation.
						// see LeafComponent#evaluateDynaAttributes
						hitann = true;
					}
				}

				if (!hitann && value != null) {
					throw new IllegalArgumentException("for JSF feature of ValueHolder, use perfix of <" + JSF_CORE_NS
							+ ">:value instead of " + attName + " in component:" + this.getClass());
				}
			}
		}

		// take care jsf value & converter.
		Object obj = _jsfcoreAttrMap.get("value");
		if (obj != null) {
			if ((obj instanceof String) && isValueReference((String) obj)) {
				ValueBinding vb = getFacesContext().getApplication().createValueBinding((String) obj);
				((BranchOutput) comp).setValueBinding("value", vb);
			} else {
				((BranchOutput) comp).setValue(obj);
			}
		}

		obj = _jsfcoreAttrMap.get("converter");
		if (obj != null) {
			if ((obj instanceof String) && isValueReference((String) obj)) {
				ValueBinding vb = getFacesContext().getApplication().createValueBinding((String) obj);
				((BranchOutput) comp).setValueBinding("converter", vb);
			} else if(obj instanceof String){
				Converter _converter = FacesContext.getCurrentInstance().getApplication().createConverter((String) obj);
				((BranchOutput) comp).setConverter(_converter);
			} else if(obj instanceof Converter){
				((BranchOutput) comp).setConverter((Converter)obj);
			} else{
				throw new RuntimeException("attribute 'converter' , unsupported type:"+obj.getClass());
			}
		}

	}

}
