/* Columns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:00:29     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.HeadersElement;

/**
 * Defines the columns of a grid.
 * Each child of a columns element should be a {@link Column} element.
 *
 * @author tomyeh
 */
public class Columns extends HeadersElement {

	/** Returns the grid that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Grid getGrid() {
		return (Grid)getParent();
	}
	
	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for columns: "+parent);
		super.setParent(parent);
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());
		final Grid grid = getGrid();
		if (grid != null)
			HTMLs.appendAttribute(sb, "z.rid", grid.getUuid());
		return sb.toString();
	}
	public boolean setVisible(boolean visible) {
		final boolean vis = super.setVisible(visible);
		final Grid grid = getGrid();
		if (grid != null)
			grid.invalidate();
		return vis;
	}
	
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Column))
			throw new UiException("Unsupported child for columns: "+child);
		return super.insertBefore(child, insertBefore);
	}
}
