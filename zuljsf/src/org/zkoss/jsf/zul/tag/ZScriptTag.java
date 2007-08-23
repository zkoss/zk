/* ZScriptTag.java

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
import org.zkoss.jsf.zul.impl.BaseZScript;
import org.zkoss.jsf.zul.tag.impl.AbstractTag;

/**
 * A JSF Tag class to handle the zscript element.
 * This tag should be declared under {@link PageTag} or any Component tags.
 * 
 * @author Dennis 
 */
public class ZScriptTag extends AbstractTag {
	

	public ZScriptTag() {
		super("ZScript");
	}
	private String _deferred = null;
	private String _lang = null;

	public void setLanguage(String lang) {
		this._lang = lang;
	}
	public void setDeferred(String deferred) {
		this._deferred = deferred;
	}
	
	public void release() {
		super.release();
		_lang=null;
		_deferred=null;
	}
	
	/**
	 * Override method, set properties to ZULJSF Component.
	 */
	protected void setProperties(UIComponent comp) {
		if(!(comp instanceof BaseZScript)){
			throw new IllegalArgumentException("Not a ZScriptComponent");
		}
		super.setProperties(comp);
		if(_lang!=null ){
			if(isValueReference(_lang)){
				throw new RuntimeException("'lang' must not a ValueReference!");
			}
			((BaseZScript)comp).setLanguage(_lang);
		}
		if(_deferred!=null ){
			if(isValueReference(_deferred)){
				throw new RuntimeException("'deferred' must not a ValueReference!");
			}
			((BaseZScript)comp).setDeferred(Boolean.parseBoolean(_deferred));
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
