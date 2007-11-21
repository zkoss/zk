/* LeafTag.java

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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.zkoss.jsf.zul.impl.AbstractComponent;
import org.zkoss.jsf.zul.impl.LeafComponent;
import org.zkoss.util.logging.Log;


/**
 * The skeletal class for implementing the generic JSF tags.
 * 
 * @author Dennis.Chen
 */
abstract public class LeafTag extends AbstractTag implements DynamicAttributes{
	private static final Log log = Log.lookup(LeafTag.class);
	/**
	 * Handle dynamic Attribute of this tag.
	 */
	protected Map _dynamicAttrMap = new LinkedHashMap();
	protected Map _jsfcoreAttrMap = new LinkedHashMap();
	
	/**
	 * Handle special namespace of attribute.
	 */
	//protected Map _specialNS = new HashMap();
	
	private String _use;
	private String _forward;
	
	
	/**
	 * constructor.
	 * @param typeName a type name with will be concatenate a {@link #COMP_TYPE_PREFIX},return at {@link #getComponentType}, 
	 */
	protected LeafTag(String typeName) {
		super(typeName);
	}
	
	public void setUse(String use){
        this._use = use;
    }
	
	public void setForward(String forward){
		_forward = forward;
	}

	public void release() {
		super.release();
		_dynamicAttrMap = null;
		_use = null;
		_forward = null;
		
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
			if("use".equals(localName)||"forward".equals(localName))
				//should not happen!!
				throw new JspException("'use', 'forward' must be set by static attribute!!!");
			if("id".equals(localName)){
				this.setId((String)value);
			}
			
		}else if(uri==null || JSF_CORE_NS.equals(uri)){
			_jsfcoreAttrMap.put(localName, value);	
			if("id".equals(localName)){
				throw new JspException("don't assign id with namespace http://java.sun.com/jsf/core, assign it with zk namespance or empty namespace");
			}else if("binding".equals(localName)){
				this.setBinding((String)value);
			}else if("rendered".equals(localName)){
				this.setRendered((String)value);
			}
		}else{
			//ignore others
		}
		
	}


	/**
	 * Override method, set properties to ZULJSF Component.
	 */
	protected void setProperties(UIComponent comp) {
		super.setProperties(comp);
		FacesContext context = ((AbstractComponent)comp).getFacesContext();
		if(_use!=null ){
			if(isValueReference(_use)){
				javax.faces.el.ValueBinding _vb = getFacesContext().getApplication().createValueBinding(_use);
				Object obj  = _vb.getValue(context);
				if(obj!=null){
					((LeafComponent)comp).setUse(obj.toString());
				}
			}else{
				((LeafComponent)comp).setUse(_use);
			}
		}
		if(_forward!=null ){
			if(isValueReference(_forward)){
				javax.faces.el.ValueBinding _vb = getFacesContext().getApplication().createValueBinding(_forward);
				Object obj  = _vb.getValue(context);
				if(obj!=null){
					((LeafComponent)comp).setForward(obj.toString());
				}
			}else{
				((LeafComponent)comp).setForward(_forward);
			}
		}
		
		LinkedHashMap compAttMap = new LinkedHashMap();
		for(Iterator itor = _dynamicAttrMap.entrySet().iterator();itor.hasNext();)
		{
			Map.Entry entry= (Entry) itor.next();
			//process set value,
			String prop = (String)entry.getKey();
			Object value = _dynamicAttrMap.get(prop);
			/*Check ZK namespace*/
			
			//if(!(value instanceof String)) throw new RuntimeException("attribute '"+prop+"' must be String");
			if(value!=null && value instanceof String && isValueReference((String)value)){
				if(prop.startsWith("on")){
					throw new RuntimeException("can not set event listener to value binding!!!");
				}
				javax.faces.el.ValueBinding _vb = getFacesContext().getApplication().createValueBinding((String)value);
				compAttMap.put(prop, _vb);
			}else{
				if(value==null){
					log.debug("A null value is set to attribute '"+prop+"'");
				}
				compAttMap.put(prop, value);
			}
		}
		((LeafComponent)comp).setZULDynamicAttribute(compAttMap);
		
	}
}
