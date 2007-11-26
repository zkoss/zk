/* InitTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/08/16  11:26:44 , Created by Dennis.Chen
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
import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.zkoss.jsf.zul.impl.AbstractComponent;
import org.zkoss.jsf.zul.impl.BaseInit;
import org.zkoss.jsf.zul.tag.impl.AbstractTag;


public class InitTag extends AbstractTag implements DynamicAttributes{

	/**
	 * Handle dynamic Attribute of this tag.
	 */
	protected Map _dynamicAttrMap = new LinkedHashMap();
	
	private String _useClass;
	
	
	/**
	 * constructor. 
	 */
	public InitTag() {
		super("Init");
	}
	
	public void setUseClass(String useClass){
        this._useClass = useClass;
    }


	public void release() {
		super.release();
		_dynamicAttrMap = null;
		_useClass = null;
		
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
			if("use".equals(localName))
				throw new JspException("please set use as a static attribute!!!");
		}else if(uri==null || JSF_CORE_NS.equals(uri)){
			if("binding".equals(localName)){
				this.setBinding((String)value);
			}else if("rendered".equals(localName)){
				this.setRendered((String)value);
			}
		}
		
	}


	/**
	 * Override method, set properties to ZULJSF Component.
	 */
	protected void setProperties(UIComponent comp) {
		super.setProperties(comp);
		FacesContext context = ((AbstractComponent)comp).getFacesContext();
		if(_useClass!=null ){
			if(isValueReference(_useClass)){
				javax.faces.el.ValueBinding _vb = getFacesContext().getApplication().createValueBinding(_useClass);
				Object obj  = _vb.getValue(context);
				if(obj!=null){
					((BaseInit)comp).setUseClass(obj.toString());
				}
			}else{
				((BaseInit)comp).setUseClass(_useClass);
			}
		}else{
			throw new RuntimeException("Set use attribute for this initiator");
		}
		
		((BaseInit)comp).setDynamicAttribute(_dynamicAttrMap);
	}
	
	
}
