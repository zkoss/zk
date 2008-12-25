/* Listcell.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug  5 13:06:17     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Iterator;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.LabelImageElement;

/**
 * A list cell.
 * 
 * <p>Default {@link #getZclass}: z-list-cell. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Listcell extends LabelImageElement implements org.zkoss.zul.api.Listcell {
	private Object _value;
	private int _span = 1;

	public Listcell() {
	}
	public Listcell(String label) {
		setLabel(label);
	}
	public Listcell(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/** Returns the list box that it belongs to.
	 */
	public Listbox getListbox() {
		final Component comp = getParent();
		return comp != null ? (Listbox)comp.getParent(): null;
	}
	/**
	 * Returns the list box that it belongs to.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listbox getListboxApi() {
		return getListbox();
	}

	public String getZclass() {
		return _zclass == null ? "z-list-cell" : _zclass;
	}
	/** Returns the list header that is in the same column as
	 * this cell, or null if not available.
	 */
	public Listheader getListheader() {
		final Listbox listbox = getListbox();
		if (listbox != null) {
			final Listhead lcs = listbox.getListhead();
			if (lcs != null) {
				final int j = getColumnIndex();
				final List lcschs = lcs.getChildren();
				if (j < lcschs.size())
					return (Listheader)lcschs.get(j);
			}
		}
		return null;
	}
	/** Returns the list header that is in the same column as
	 * this cell, or null if not available.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listheader getListheaderApi() {
		return getListheader();
	}

	/** Returns the column index of this cell, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator();
		it.hasNext(); ++j)
			if (it.next() == this)
				break;
		return j;
	}

	/** Returns the maximal length for this cell.
	 * If listbox's mold is "select", it is the same as
	 * {@link Listbox#getMaxlength}
	 * If not, it is the same as the correponding {@link #getListheader}'s 
	 * {@link Listheader#getMaxlength}.
	 *
	 * <p>Note: {@link Listitem#getMaxlength} is the same as {@link Listbox#getMaxlength}.
	 */
	public int getMaxlength() {
		final Listbox listbox = getListbox();
		if (listbox == null)
			return 0;
		if (listbox.inSelectMold())
			return listbox.getMaxlength();
		final Listheader lc = getListheader();
		return lc != null ? lc.getMaxlength(): 0;
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 */
	public Object getValue() {
		return _value;
	}
	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 */
	public void setValue(Object value) {
		_value = value;
	}

	/** Returns number of columns to span this cell.
	 * Default: 1.
	 */
	public int getSpan() {
		return _span;
	}
	/** Sets the number of columns to span this cell.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span) {
		if (_span != span) {
			_span = span;
			smartUpdate("colspan", _span);
		}
	}
	
	//-- super --//
	public void setWidth(String width) {
		throw new UnsupportedOperationException("Set listheader's width instead");
	}

	//-- Component --//
 	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Listitem))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
}
