/* PageTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 20 11:32:56     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.tag;

import javax.faces.component.UIComponent;

import org.zkoss.jsf.zul.Page;
import org.zkoss.jsf.zul.tag.impl.RootTag;

/**
 *
 * @author Dennis.Chen
 */
public class PageTag extends RootTag {

	public PageTag() {
		super("Page");
	}
	private String _style;

	public void setStyle(String style) {
		_style = style ;
	}
	
	/**
	 * Override method, set properties to ZULJSF Component.
	 */
	protected void setProperties(UIComponent comp) {
		if(!(comp instanceof Page)){
			throw new IllegalArgumentException("Not a Page:"+comp);
		}
		super.setProperties(comp);
		if(_style!=null ){
			if(isValueReference(_style)){
				throw new RuntimeException("'style' must not a ValueReference!");
			}
			((Page)comp).setStyle(_style);
		}
	}

	public void release() {
		super.release();
		_style = null;
		
	}



}
