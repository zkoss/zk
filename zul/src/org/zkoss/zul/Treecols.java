/* Treecols.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:55:52     2005, Created by tomyeh
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
 * A treecols.
 *
 * @author tomyeh
 */
public class Treecols extends XulElement {
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

	/** Returns the tree that it belongs to.
	 */
	public Tree getTree() {
		return (Tree)getParent();
	}

	//super//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		return _sizeable ? attrs + " z.sizeable=\"true\"": attrs;
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Tree))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Treecol))
			throw new UiException("Unsupported child for treecols: "+child);
		return super.insertBefore(child, insertBefore);
	}
}
