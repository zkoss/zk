/* AttributeTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 27 17:09:09     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.tag;

import javax.faces.component.UIComponent;
import javax.servlet.jsp.JspException;

import org.zkoss.jsf.zul.impl.AbstractComponent;
import org.zkoss.jsf.zul.impl.BaseAttribute;
import org.zkoss.jsf.zul.tag.impl.AbstractTag;


/**
 * A JSF Tag class to handle the attribute element.
 * @author Dennis.Chen
 */
public class AttributeTag extends AbstractTag {
	
	private String _name;
	
	public AttributeTag() {
		super("Attribute");
	}
	

	
	/**
	 * set attribute's key property 
	 * @param name
	 */
	public void setName(String name) {
		this._name = name;
	}
	public void release() {
		super.release();
		_name=null;
	}
	
	/**
	 * Override method, set properties to ZULJSF Component.
	 */
	protected void setProperties(UIComponent comp) {
		if(!(comp instanceof BaseAttribute)){
			throw new IllegalArgumentException("Not a AttributeComponent");
		}
		super.setProperties(comp);
		
		if(_name!=null){
			if(isValueReference(_name)){
				throw new RuntimeException("attribute 'name' can not be a ValueReference!");
			}
			((BaseAttribute)comp).setName(_name);
		}else{
			throw new NullPointerException("attribute 'name' can not be null!");
		}
		
	}
	
	/**
	 * Override Method,
	 * Set String of BodyContent into instance of ZULJSF Component which associated to this Tag.
	 * Ingore the supressed status. 
	 * @see org.zkoss.jsf.zul.impl.AbstractComponent
	 */
    public int doAfterBody() throws JspException {
        if (!isSkipBody() && getBodyContent() != null) {
            String value = getBodyContent().getString();
            //if is supressed, then component's bodycontent will set by children.
            if (value != null) {
            	value = value.trim();
            	AbstractComponent comp = (AbstractComponent) getComponentInstance();
            	comp.setBodyContent(value);
            }
        }
        return (getDoAfterBodyValue());
    }

}
