/* Anchorchildren.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct  3 11:05:38 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.impl.XulElement;

/**
 * The children of Anchorlayout. <br> 
 * Can accept any ZK component as child.
 * 
 * <p>Default {@link #getZclass}: z-anchorchildren.
 * @author peterkuo
 * @since 6.0.0
 */
public class Anchorchildren extends XulElement{

	/** accept number, percent, space separated*/
	private String _anchor = ""; 
	
	public Anchorchildren(){}
	
	public Anchorchildren(String anchor){
		super();
		_anchor = anchor;
	}
	
	/**
	 * Returns the anchor setting.
	 */
	public String getAnchor(){
		return _anchor;
	}
	
	/**
	 * Sets the width, height relative to parent, anchorlayout.
	 * It can use % or number.
	 * Accept one argument, or two argument separated by space.
	 * The first argument is for width, and second for height.
	 * For example, "50% 50%" means the anchorchildren width and height is 50%
	 * of {@link Anchorlayout}.
	 * "-30 20%" means the width is 20px less than parent, and height is 20% of parent.
	 * "50%" means the width is 50% of parent, and the height is no assumed. 
	 * @param anchor
	 */
	public void setAnchor(String anchor) {
		if (anchor == null)
			anchor = "";
		if(!Objects.equals(_anchor,anchor)){
			_anchor = anchor;
			smartUpdate("anchor",_anchor);
		}
	}

	public String getZclass() {
		return _zclass == null ? "z-anchorchildren" : _zclass;
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Anchorlayout))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}
	
	protected void renderProperties(ContentRenderer renderer) throws IOException{
		super.renderProperties(renderer);
				
		render(renderer,"anchor", _anchor);
	}
}
