/* Listhead.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug  5 13:06:38     2005, Created by tomyeh
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
 * A list headers used to define multi-columns and/or headers.
 *
 * <p>Difference from XUL:
 * <ol>
 * <li>There is no listcols in ZUL because it is merged into {@link Listhead}.
 * Reason: easier to write Listbox.</li>
 * </ol>
 *
 * @author tomyeh
 */
public class Listhead extends XulElement {
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

	/** Returns the list box that it belongs to.
	 */
	public Listbox getListbox() {
		return (Listbox)getParent();
	}

	//super//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		return _sizeable ? attrs + " z.sizeable=\"true\"": attrs;
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Listbox))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Listheader))
			throw new UiException("Unsupported child for listhead: "+child);
		return super.insertBefore(child, insertBefore);
	}
}
