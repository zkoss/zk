/* Columns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:00:29     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

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
 * Defines the columns of a grid.
 * Each child of a columns element should be a {@link Column} element.
 *
 * @author tomyeh
 */
public class Columns extends XulElement {
	private boolean _sizeable;

	/** Returns whether the width of the child column is sizeable.
	 */
	public boolean isSizeable() {
		return _sizeable;
	}
	/** Sets whether the width of the child column is sizeable.
	 * If true, an user can drag the border between two columns ({@link Column})
	 * to change the widths of adjacent columns.
	 * <p>Default: false.
	 */
	public void setSizeable(boolean sizeable) {
		if (_sizeable != sizeable) {
			_sizeable = sizeable;
			smartUpdate("z.sizeable", sizeable);
		}
	}

	//super//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		return _sizeable ? attrs + " z.sizeable=\"true\"": attrs;
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for columns: "+parent);
		final boolean changed = getParent() != parent;
		super.setParent(parent);
		if (changed && parent != null) ((Grid)parent).initAtClient();
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Column))
			throw new UiException("Unsupported child for columns: "+child);
		if (super.insertBefore(child, insertBefore)) {
			final Component parent = getParent();
			if (parent != null) parent.invalidate();
				//FUTURE: optimize the invalidation to attributes of
				//certain cells
				//It implies initAtClient
			return true;
		}
		return false;
	}
	public boolean removeChild(Component child) {
		if (super.removeChild(child)) {
			final Component parent = getParent();
			if (parent != null) parent.invalidate();
				//FUTURE: optimize the invalidation to attributes of
				//certain cells
				//It implies initAtClient
			return true;
		}
		return false;
	}
}
