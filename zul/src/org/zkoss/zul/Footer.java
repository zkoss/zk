/* Footer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 12:27:04     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Iterator;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * A column of the footer of a grid ({@link Grid}).
 * Its parent must be {@link Foot}.
 *
 * <p>Unlike {@link Column}, you could place any child in a grid footer.
 * <p>Default {@link #getZclass}: z-footer.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Footer  extends LabelImageElement implements org.zkoss.zul.api.Footer {
	private int _span = 1;

	public Footer() {
	}
	public Footer(String label) {
		setLabel(label);
	}
	public Footer(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/** Returns the grid that this belongs to.
	 */
	public Grid getGrid() {
		final Component comp = getParent();
		return comp != null ? (Grid)comp.getParent(): null;
	}
	/** Returns the grid that this belongs to.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Grid getGridApi() {
		return getGrid();
	}
	/** Returns the column index, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator();
		it.hasNext(); ++j)
			if (it.next() == this)
				break;
		return j;
	}
	/** Returns the column that is in the same column as
	 * this footer, or null if not available.
	 */
	public Column getColumn() {
		final Grid grid = getGrid();
		if (grid != null) {
			final Columns cs = grid.getColumns();
			if (cs != null) {
				final int j = getColumnIndex();
				final List cschs = cs.getChildren();
				if (j < cschs.size())
					return (Column)cschs.get(j);
			}
		}
		return null;
	}
	/** Returns the column that is in the same column as
	 * this footer, or null if not available.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Column getColumnApi() {
		return getColumn();
	}
	
	/** Returns number of columns to span this footer.
	 * Default: 1.
	 */
	public int getSpan() {
		return _span;
	}
	/** Sets the number of columns to span this footer.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span) {
		if (_span != span) {
			_span = span;
			smartUpdate("colspan", _span);
		}
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-footer" : _zclass;
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Foot))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
}
