/* Footer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 12:27:04     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * A column of the footer of a grid ({@link Grid}).
 * Its parent must be {@link Foot}.
 *
 * <p>Unlike {@link Column}, you could place any child in a grid footer.
 *
 * @author tomyeh
 */
public class Footer  extends LabelImageElement {
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
	/** Returns the set of footers that this belongs to.
	 */
	public Foot getFoot() {
		return (Foot)getParent();
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

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());

		final String clkattrs = getAllOnClickAttrs(false);
		if (clkattrs != null) sb.append(clkattrs);

		final Column col = getColumn();
		if (col != null) sb.append(col.getColAttrs());

		return sb.toString();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Foot))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
}
