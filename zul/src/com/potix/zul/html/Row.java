/* Row.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:43     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;
import java.util.Iterator;
import java.io.IOException;

import com.potix.lang.Objects;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zul.html.impl.XulElement;

/**
 * A single row in a {@link Rows} element.
 * Each child of the {@link Row} element is placed in each successive cell
 * of the grid. The row with the most child elements determines the number
 * of columns in each row.
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.8 $ $Date: 2006/05/29 04:28:26 $
 */
public class Row extends XulElement {
	private String _align, _valign;
	private boolean _nowrap;

	/** Returns the grid that contains this row. */
	public Grid getGrid() {
		final Component parent = getParent();
		return parent != null ? (Grid)parent.getParent(): null;
	}

	/** Returns the horizontal alignment of the whole cell.
	 * <p>Default: null (system default: left unless CSS specified).
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the horizontal alignment of the whole cell.
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}
	/** Returns the nowrap.
	 * <p>Default: null (system default: wrap).
	 */
	public boolean getNowrap() {
		return _nowrap;
	}
	/** Sets the nowrap.
	 */
	public void setNowrap(boolean nowrap) {
		if (_nowrap != nowrap) {
			_nowrap = nowrap;
			smartUpdate("nowrap", _nowrap);
		}
	}
	/** Returns the vertical alignment of the whole cell.
	 * <p>Default: null (system default: top).
	 */
	public String getValign() {
		return _valign;
	}
	/** Sets the vertical alignment of the whole cell.
	 */
	public void setValign(String valign) {
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdate("valign", _valign);
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "align", _align);
		HTMLs.appendAttribute(sb, "valign", _valign);
		if (_nowrap)
			HTMLs.appendAttribute(sb, "nowrap", "nowrap");
		return sb.toString();
	}

	//-- Component --//
	public void invalidate(Range range) {
		super.invalidate(range);
		initAtClient();
	}
	private void initAtClient() {
		final Grid grid = getGrid();
		if (grid != null) grid.initAtClient(); //because width might change
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Rows))
			throw new UiException("Unsupported parent for row: "+parent);
		super.setParent(parent);
	}
	public void onDrawNewChild(Component child, StringBuffer out)
	throws IOException {
		final StringBuffer sb = new StringBuffer(128)
			.append("<td id=\"").append(child.getUuid())
			.append("!chdextr\" class=\"gridev\"");

		final Grid grid = getGrid();
		if (grid != null) {
			final Columns cols = grid.getColumns();
			if (cols != null) {
				int j = 0;
				for (Iterator it = getChildren().iterator(); it.hasNext(); ++j)
					if (child == it.next())
						break;
				final List colchds = cols.getChildren();
				if (j < colchds.size())
					sb.append(((Column)colchds.get(j)).getColAttrs());
			}
		}
		sb.append('>');
		out.insert(0, sb);
		out.append("</td>");
	}
}
