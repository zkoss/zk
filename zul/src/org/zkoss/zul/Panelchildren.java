/* Panelchildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 10, 2008 11:47:15 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

/**
 * Panelchildren is used for {@link Panel} component to manage each child who will
 * be shown in the body of Panel.
 * Note that the size of Panelchildren is automatically calculated by Panel so both
 * {@link #setWidth(String)} and {@link #setHeight(String)} are read-only.
 * 
 * <p>Default {@link #getMoldSclass}: z-panel-children.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class Panelchildren extends XulElement {
	
	public Panelchildren() {
		setMoldSclass("z-panel-children");
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Panel))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	
	/**
	 * This method is unsupported. Please use {@link Panel#setWidth(String)} instead.
	 */
	public void setWidth(String width) {
		throw new UnsupportedOperationException("readonly");
	}
	/**
	 * This method is unsupported. Please use {@link Panel#setHeight(String)} instead.
	 */
	public void setHeight(String height) {
		throw new UnsupportedOperationException("readonly");
	}
	
	/** Returns the real style class used for the content block of the panel.
	 *
	 * <p>If {@link #setSclass} was called with a non-empty value,
	 * say, "z-panel-children", then
	 * <ol>
	 * <li>Case 1: If {@link Panel#getBorder} is "normal", "z-panel-children" is returned.</li>
	 * <li>Case 2: Otherwise, "z-panel-children-<i>border</i>" is returned
	 * where <i>border</i> is the value returned by {@link Panel#getBorder()}.</li>
	 * <li>Case 3: If {@link Panel#getTitle()} and {@link Panel#getCaption()} are
	 * null, "z-panel-children-notitle" is returned with above cases.</li>
	 * </ol>
	 */
	public String getRealSclass() {
		final String scls = super.getRealSclass();
		final String moldsclass = getMoldSclass();
		final Panel parent = (Panel) getParent();
		if (parent != null) {
			final String title = moldsclass != null && parent.getTitle().length() == 0 
				&& parent.getCaption() == null ? moldsclass + "-notitle" : "";
			final String border = parent.getBorder();
			return scls + ("normal".equals(border) ? "" : ' ' + scls + '-' + border) + (title.length() > 0 ? ' ' + title : title);
		}
		return scls;
			
	}
}
