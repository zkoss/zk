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

import org.zkoss.jsf.zul.impl.BaseUi;
import org.zkoss.jsf.zul.tag.impl.BranchTag;

/**
 * A JSF Tag class to handle the macro component element.
 * This tag should be declared under {@link PageTag} or any Component tags.
 * 
 * @author Dennis 
 */
public class UiTag extends BranchTag {
	

	public UiTag() {
		super("Ui");
	}
	private String _tag = null;
	
	public void setTag(String tag) {
		this._tag = tag;
	}
	
	public void release() {
		super.release();
		_tag=null;
	}
	
	/**
	 * Override method, set properties to ZULJSF Component.
	 */
	protected void setProperties(UIComponent comp) {
		if(!(comp instanceof BaseUi)){
			throw new IllegalArgumentException("Not a Ui");
		}
		super.setProperties(comp);
		if(_tag!=null ){
			if(isValueReference(_tag)){
				throw new RuntimeException("'tag' must not a ValueReference!");
			}
			((BaseUi)comp).setTag(_tag);
		}
	}
}
