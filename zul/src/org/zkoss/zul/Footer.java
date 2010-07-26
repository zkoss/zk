/* Footer.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 12:27:04     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.HeaderElement;

/**
 * A column of the footer of a grid ({@link Grid}).
 * Its parent must be {@link Foot}.
 *
 * <p>Unlike {@link Column}, you could place any child in a grid footer.
 * <p>Default {@link #getZclass}: z-footer.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Footer  extends HeaderElement implements org.zkoss.zul.api.Footer {
	private int _span = 1;

	public Footer() {
	}
	public Footer(String label) {
		super(label);
	}
	public Footer(String label, String src) {
		super(label, src);
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
			smartUpdate("span", _span);
		}
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-footer" : _zclass;
	}

	//-- Component --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		if (_span > 1)
			renderer.render("span", _span);

		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
	}
	
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Foot))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
}
