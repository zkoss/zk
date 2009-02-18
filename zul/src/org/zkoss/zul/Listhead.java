/* Listhead.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug  5 13:06:38     2005, Created by tomyeh
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
 * A list headers used to define multi-columns and/or headers.
 *
 * <p>Difference from XUL:
 * <ol>
 * <li>There is no listcols in ZUL because it is merged into {@link Listhead}.
 * Reason: easier to write Listbox.</li>
 * </ol>
 *  <p>Default {@link #getZclass}: z-list-head.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Listhead extends HeadersElement implements org.zkoss.zul.api.Listhead {
	private Object _value;

	/** Returns the list box that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Listbox getListbox() {
		return (Listbox)getParent();
	}
	/** Returns the list box that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listbox getListboxApi() {
		return getListbox();
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 3.6.0
	 */
	public Object getValue() {
		return _value;
	}
	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 3.6.0
	 */
	public void setValue(Object value) {
		_value = value;
	}

	//super//
	public boolean setVisible(boolean visible) {
		final boolean vis = super.setVisible(visible);
		final Listbox listbox = getListbox();
		if (listbox != null)
			listbox.invalidate();
		return vis;
	}

	public String getZclass() {
		return _zclass == null ? "z-list-head" : _zclass;
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());
		final Listbox listbox = getListbox();
		if (listbox != null)
			HTMLs.appendAttribute(sb, "z.rid", listbox.getUuid());
		return sb.toString();
	}

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
