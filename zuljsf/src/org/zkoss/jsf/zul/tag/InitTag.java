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

import javax.faces.component.UIComponent;

import org.zkoss.jsf.zul.tag.impl.BranchTag;


public class InitTag extends BranchTag {

	/**
	 * instance Class which implement Initiator.
	 */
	private String _clazz = null;
	
	public InitTag() {
		super("Init");
	}

	public void release() {
		super.release();
		_clazz=null;
	}
	
	public void setClass(String clazz){
		this._clazz = clazz;
	}
	
	/**
	 * Override method, set properties to ZULJSF Component.
	 */
	protected void setProperties(UIComponent comp) {
		/*if(!(comp instanceof BaseInit)){
			throw new IllegalArgumentException("Not a ZScriptComponent");
		}
		super.setProperties(comp);
		if(_clazz!=null ){
			if(isValueReference(_clazz)){
				throw new RuntimeException("'class' must not a ValueReference!");
			}
			//((BaseInit)comp).setLanguage(_clazz);
		}*/
	}
	
}
