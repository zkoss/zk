/* RootTag.java

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

import org.zkoss.jsf.zul.impl.RootComponent;


/**
 * The skeletal class for implementing the generic JSF tags.
 * 
 * @author Dennis.Chen
 */
abstract public class RootTag extends AbstractTag{
	/**
	 * constructor.
	 * @param typeName a type name with will be concatenate a {@link #COMP_TYPE_PREFIX},return at {@link #getComponentType}, 
	 */
	protected RootTag(String typeName) {
		super(typeName);
	}
	private String _lang ;

	
	public void setZScriptLanguage(String lang) {
		_lang = lang != null ? lang: "Java";
	}

	public void setZscriptLanguage(String lang) {
		setZScriptLanguage(lang);
	}

	public void release() {
		super.release();
		_lang=null;
	}
	
	/**
	 * Override method, set properties to ZULJSF Component.
	 */
	protected void setProperties(UIComponent comp) {
		if(!(comp instanceof RootComponent)){
			throw new IllegalArgumentException("Not a RootComponent");
		}
		super.setProperties(comp);
		if(_lang!=null ){
			if(isValueReference(_lang)){
				throw new RuntimeException("'lang' must not a ValueReference!");
			}
			((RootComponent)comp).setZScriptLanguage(_lang);
		}
	}
	
	
	
	

}
