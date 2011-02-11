/* MeshElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Feb 11, 2011 5:48:26 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/


package org.zkoss.zul.impl;

/**
 * The fundamental class for mesh elements such as {@link Grid}, {@link Listbox}, and {@link Tree}.

 * @author henrichen
 * @since 5.0.6
 */
public class MeshElement extends XulElement {
	private boolean _span; //since 5.0.5
	
	/**
	 * Sets whether to span the width of the columns to occupy the whole grid. It 
	 * is meaningful only if there are extra space available for columns.
	 * <p>Default: false. It means the width of a column takes only specified
	 * space based on its setting even there are extra space.
	 * @param span whether to span the width of the columns to occupy the whole grid.
	 * @since 5.0.5
	 */
	public void setSpan(boolean span) {
		if (_span != span) {
			_span = span;
			smartUpdate("span", span);
		}
	}
	/**
	 * Returns whether span column width when there are extra space.
	 * <p>Default: false.
	 * @return whether span column width when there are extra space.
	 * @since 5.0.5
	 */
	public boolean isSpan() {
		return _span;
	}
	
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		renderer.render("span", _span);
	}
}
