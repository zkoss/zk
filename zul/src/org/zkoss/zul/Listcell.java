/* Listcell.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug  5 13:06:17     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * A list cell.
 * 
 * <p>Default {@link #getZclass}: z-listcell (since 5.0.0)
 *
 * @author tomyeh
 */
public class Listcell extends LabelImageElement {
	private Object _value;
	private AuxInfo _auxinf;

	public Listcell() {
	}

	public Listcell(String label) {
		super(label);
	}

	public Listcell(String label, String src) {
		super(label, src);
	}

	/** Returns the list box that it belongs to.
	 */
	public Listbox getListbox() {
		final Component comp = getParent();
		return comp != null ? (Listbox) comp.getParent() : null;
	}

	public String getZclass() {
		return _zclass == null ? "z-listcell" : _zclass;
	}

	//Cloneable//
	public Object clone() {
		final Listcell clone = (Listcell) super.clone();
		if (_auxinf != null)
			clone._auxinf = (AuxInfo) _auxinf.clone();
		return clone;
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
					return (Listheader) lcschs.get(j);
			}
		}
		return null;
	}

	/** Returns the column index of this cell, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator(); it.hasNext(); ++j)
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
		return lc != null ? lc.getMaxlength() : 0;
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) _value;
	}

	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 */
	public <T> void setValue(T value) {
		_value = value;
	}

	/** Returns number of columns to span this cell.
	 * Default: 1.
	 */
	public int getSpan() {
		return _auxinf != null ? _auxinf.span : 1;
	}

	/** Sets the number of columns to span this cell.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span) {
		if (getSpan() != span) {
			initAuxInfo().span = span;
			smartUpdate("colspan", getSpan());
		}
	}

	/**
	 * @deprecated as of release 6.0.0. To control the hflex of Listcell, please 
	 * use {@link Listheader#setHflex(String)} instead.
	 */
	public void setHflex(String flex) {
	}

	//-- super --//
	public void setWidth(String width) {
		throw new UnsupportedOperationException("Set listheader's width instead");
	}

	//-- Component --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		if (getSpan() > 1)
			renderer.render("colspan", getSpan());
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Listitem))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}

	private AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}

	private static class AuxInfo implements java.io.Serializable, Cloneable {
		private int span = 1;

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
	
	protected void addMoved(Component oldparent, Page oldpg, Page newpg) {
		super.addMoved(oldparent, oldpg, newpg);
	}
}
