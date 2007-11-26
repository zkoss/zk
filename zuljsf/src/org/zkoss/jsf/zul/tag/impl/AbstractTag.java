/* AbstractTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8 10:00:10     2007, Created by Dennis Chen
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
import javax.faces.webapp.UIComponentBodyTag;
import javax.servlet.jsp.JspException;

import org.zkoss.jsf.zul.impl.AbstractComponent;

/**
 * The skeletal class for implementing the generic JSF tags.
 * 
 * @author Dennis.Chen
 */
abstract public class AbstractTag extends UIComponentBodyTag {

	private String _if;
	private String _unless;
	private String _typeName;
	private boolean _isSkipBody = false;
	private AbstractComponent _comp = null;
	
	protected static final String COMP_TYPE_PREFIX = "org.zkoss.jsf.zul.";
	//renderer isn't implement yet.
	//protected static String RENDERER_TYPE_PREFIX = "org.zkoss.jsf.zul.renderer";
	
	protected static String ZUL_JSF_NS = "http://www.zkoss.org/jsf/zul";
	protected static String JSF_HTML_NS = "http://java.sun.com/jsf/html";
	protected static String JSF_CORE_NS = "http://java.sun.com/jsf/core";
	protected static String JSF_CORE_PREFIX = "f_";
	
	/**
	 * constructor.
	 * @param typeName a type name with will be concatenate a {@link #COMP_TYPE_PREFIX},return at {@link #getComponentType}, 
	 */
	protected AbstractTag(String typeName){
		this._typeName = typeName;
	}

	/** Sets the if condition.
	 */
	public void setIf(String ifcond) {
		_if = ifcond;
	}
	public void setUnless(String unless) {
		_unless = unless;
	}
	
	/**
	 * Override method, called by UIComponentTag.
	 * return SKIP_BODY if component shouldn't crate, or EVAL_BODY_BUFFERED otherwise 
	 * @return SKIP_BODY or EVAL_BODY_BUFFERED
	 */
	protected int getDoStartValue() throws JspException {
		//if _comp==null , that means it is submit, _comp will restore suppressed in state.
		if(_comp!=null){
			_comp.setSuppressed(isSuppressed());
		}
        return _isSkipBody?(SKIP_BODY):(EVAL_BODY_BUFFERED);

    }
	
	public void release() {
		super.release();
		_if = null;
		_unless = null;
		_isSkipBody = false;
		_typeName = null;
		_comp = null;
	}


	/**
	 * Override method, set properties of ZULJSF Component
	 */
	protected void setProperties(UIComponent comp) {
		if(!(comp instanceof AbstractComponent)){
			throw new IllegalArgumentException("Not a AbstractComponent");
		}
		super.setProperties(comp);
		_comp = (AbstractComponent)comp;
		FacesContext context = _comp.getFacesContext();
		if(_if!=null ){
			if(isValueReference(_if)){
				javax.faces.el.ValueBinding _vb = getFacesContext().getApplication().createValueBinding(_if);
				Object obj  = _vb.getValue(context);
				if(obj!=null){
					if(obj instanceof Boolean){
						_comp.setIf(((Boolean)obj).booleanValue());
					}else{
						_comp.setIf(Boolean.parseBoolean(obj.toString()));
					}
				}
			}else{
				_comp.setIf(Boolean.parseBoolean(_if));
			}
		}
		if(_unless!=null ){
			if(isValueReference(_unless)){
				javax.faces.el.ValueBinding _vb = getFacesContext().getApplication().createValueBinding(_unless);
				Object obj  = _vb.getValue(context);
				if(obj!=null){
					if(obj instanceof Boolean){
						_comp.setUnless(((Boolean)obj).booleanValue());
					}else{
						_comp.setUnless(Boolean.parseBoolean(obj.toString()));
					}
				}
			}else{
				_comp.setUnless(Boolean.parseBoolean(_unless));
			}
		}
		
		if(!_comp.isEffective()){
			_comp.setRendered(false);
			_isSkipBody = true;
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		return COMP_TYPE_PREFIX+_typeName;
	}
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
		//Renderer type not implement yet.
		return null;//RENDERER_TYPE_PREFIX+_typeName;
	}
	
	/**
	 * check should this tag skip the body content.
	 */
	protected boolean isSkipBody(){
		return _isSkipBody;
	}
	
	/**
	 * Override method, set String of BodyContent into instance of ZULJSF Component which associated to this Tag. 
	 * @see org.zkoss.jsf.zul.impl.AbstractComponent
	 */
    public int doAfterBody() throws JspException {
        if (!isSkipBody() && getBodyContent() != null) {
            String value = getBodyContent().getString();
            //if is suppressed, then component's bodycontent will set by children.
            if (value != null && !isSuppressed()) {
            	value = value.trim();
            	AbstractComponent comp = (AbstractComponent) getComponentInstance();
            	comp.setBodyContent(value);
            }
        }
        return (getDoAfterBodyValue());
    }
    
    /**
     * check if the name is a special jsf core attribute. The jsf core attribute name must start with 'f_' prefix
     */
    protected String checkSpeciaJSFCoreAttribute(String name){
    	if(name==null) return name;
    	if(name.startsWith(JSF_CORE_PREFIX)){
    		return name.substring(JSF_CORE_PREFIX.length(),name.length());
    	}
    	return null;
    }

}
