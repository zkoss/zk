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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.zkoss.jsf.zul.impl.BaseCustomAttributes;
import org.zkoss.jsf.zul.tag.impl.AbstractTag;


/**
 * A JSF Tag class to handle the attribute element.
 * @author Dennis.Chen
 */
public class CustomAttributesTag extends AbstractTag implements DynamicAttributes{
	
	/**
	 * Handle dynamic Attribute of this tag.
	 */
	protected Map _dynamicAttrMap = new LinkedHashMap();

	public CustomAttributesTag() {
		super("CustomAttributes");
	}
	
	public void release() {
		super.release();
		_dynamicAttrMap = null;
	}
	/**
	 *   Called when a tag declared to accept dynamic attributes is passed an 
	 *   attribute that is not declared in the Tag Library Descriptor.<br>
	 *   
	 * @param uri the namespace of the attribute
	 * @param localName the name of the attribute being set.
	 * @param value  the value of the attribute
	 */
	public void setDynamicAttribute(String uri, String localName, Object value) 
	throws JspException {
		if(uri==null || ZUL_JSF_NS.equals(uri)){
			_dynamicAttrMap.put(localName, value);
		}
	}


	/**
	 * Override method, set properties to ZULJSF Component.
	 */
	protected void setProperties(UIComponent comp) {
		super.setProperties(comp);
		((BaseCustomAttributes)comp).setDynamicAttribute(_dynamicAttrMap);
	}

}
