/* Row.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:43     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

/**
 * A single row in a {@link Rows} element.
 * Each child of the {@link Row} element is placed in each successive cell
 * of the grid. The row with the most child elements determines the number
 * of columns in each row.
 *
 * @author tomyeh
 */
public class Row extends XulElement {
	private Object _value;
	private String _align, _valign;
	private int[] _spans;
	/** Used only to generate {@link #getChildAttrs}. */
	private transient int _rsflags;
	private boolean _nowrap;

	/** Returns the grid that contains this row. */
	public Grid getGrid() {
		final Component parent = getParent();
		return parent != null ? (Grid)parent.getParent(): null;
	}

	/** Returns the horizontal alignment of the whole row.
	 * <p>Default: null (system default: left unless CSS specified).
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the horizontal alignment of the whole row.
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
	public boolean isNowrap() {
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
	/** Returns the vertical alignment of the whole row.
	 * <p>Default: null (system default: top).
	 */
	public String getValign() {
		return _valign;
	}
	/** Sets the vertical alignment of the whole row.
	 */
	public void setValign(String valign) {
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdate("valign", _valign);
		}
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

	/** Returns the spans, which is a list of numbers separated by comma.
	 *
	 * <p>Default: empty.
	 */
	public String getSpans() {
		if (_spans == null)
			return "";

		final StringBuffer sb = new StringBuffer(50);
		for (int j = 0;;) {
			sb.append(_spans[j]);
			if (++j >= _spans.length) {
				sb.append(',');
				break;
			}
		}
		return sb.toString();
	}
	/** Sets the spans, which is a list of numbers separated by comma.
	 *
	 * <p>For example, "1,2,3" means the second column will span two columns
	 * and the following column span three columns, while others occupies
	 * one column.
	 */
	public void setSpans(String spans) {
		List lspans = new LinkedList();
		if (spans != null) {
			for (int j = 0;;) {
				int k = spans.indexOf(',', j);
				final String s =
					(k >= 0 ? spans.substring(j, k): spans.substring(j)).trim();
				if (s.length() == 0) {
					if (k < 0) break;
					lspans.add(new Integer(1));
				} else {
					//TODO
				}	
				if (k < 0) break;
			}
		}

		int[] spansVal;
		final int sz = lspans.size();
		if (sz > 0) {
			spansVal = new int[sz];
		} else {
			spansVal = null;
		}

		if (!Objects.equals(spansVal, _spans)) {
			_spans = spansVal;
			invalidate();
		}
	}

	/** Returns the HTML attributes for the child of the specified index.
	 */
	public String getChildAttrs(int index) {
		String colattrs = null, wd = null, hgh = null;
		final Grid grid = getGrid();
		if (grid != null) {
			final Columns cols = grid.getColumns();
			if (cols != null) {
				final List colchds = cols.getChildren();
				if (index < colchds.size()) {
					final Column col = (Column)colchds.get(index);
					colattrs = col.getColAttrs();
					wd = col.getWidth();
					hgh = col.getHeight();
				}
			}
		}

		String style;
		_rsflags = RS_NO_WIDTH|RS_NO_HEIGHT|RS_NO_DISPLAY;
		try {
			style = getRealStyle();
		} finally {
			_rsflags = 0;
		}

		if (wd != null || hgh != null) {
			final StringBuffer sb = new StringBuffer(80).append(style);
			HTMLs.appendStyle(sb, "width", wd);
			HTMLs.appendStyle(sb, "height", hgh);
			style = sb.toString();
		}

		final String sclass = getSclass();
		if (colattrs == null && sclass == null && style.length() == 0)
			return "";

		final StringBuffer sb = new StringBuffer(100);
		if (colattrs != null) sb.append(colattrs);
		HTMLs.appendAttribute(sb, "class", sclass);
		HTMLs.appendAttribute(sb, "style", style);
		return sb.toString();
	}

	//-- super --//
	protected int getRealStyleFlags() {
		return super.getRealStyleFlags() | _rsflags;
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		final String clkattrs = getAllOnClickAttrs(false);
		if (clkattrs != null)
			sb.append(clkattrs);
		HTMLs.appendAttribute(sb, "align", _align);
		HTMLs.appendAttribute(sb, "valign", _valign);
		if (_nowrap)
			HTMLs.appendAttribute(sb, "nowrap", "nowrap");
		return sb.toString();
	}
	public void setStyle(String style) {
		if (style != null && style.length() == 0) style = null;

		final String s = getStyle();
		if (!Objects.equals(s, style)) {
			super.setStyle(style);
			invalidate(); //yes, invalidate
		}
	}
	public void setSclass(String sclass) {
		if (sclass != null && sclass.length() == 0) sclass = null;

		final String s = getSclass();
		if (!Objects.equals(s, sclass)) {
			super.setSclass(sclass);
			invalidate(); //yes, invalidate
		}
	}

	//-- Component --//
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
				sb.append(getChildAttrs(j));
			}
		}
		sb.append('>');
		out.insert(0, sb);
		out.append("</td>");
	}
}
