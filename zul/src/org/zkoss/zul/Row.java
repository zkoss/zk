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
import org.zkoss.zk.ui.WrongValueException;
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
		return numbersToString(_spans);
	}
	/** Sets the spans, which is a list of numbers separated by comma.
	 *
	 * <p>For example, "1,2,3" means the second column will span two columns
	 * and the following column span three columns, while others occupies
	 * one column.
	 */
	public void setSpans(String spans) throws WrongValueException {
		final int[] ispans = parseNumbers(spans, 1);
		if (!Objects.equals(ispans, _spans)) {
			_spans = ispans;
			invalidate();
		}
	}
	/** Parse a list of numbers.
	 *
	 * @param defaultValue the value if a number is omitted. For example, ",2"
	 * means "1,2" if defafultValue is 1
	 * @return an array of int, or null if no integer at all
	 */
	/*package*/ static final int[] parseNumbers(String numbers, int defaultValue)
	throws WrongValueException {
		if (numbers == null)
			return null;

		List list = new LinkedList();
		for (int j = 0;;) {
			int k = numbers.indexOf(',', j);
			final String s =
				(k >= 0 ? numbers.substring(j, k): numbers.substring(j)).trim();
			if (s.length() == 0) {
				if (k < 0) break;
				list.add(null);
			} else {
				try {
					list.add(Integer.valueOf(s));
				} catch (Throwable ex) {
					throw new WrongValueException("Not a valid number list: "+numbers);
				}
			}	

			if (k < 0) break;
			j = k + 1;
		}

		int[] ary;
		final int sz = list.size();
		if (sz > 0) {
			ary = new int[sz];
			int j = 0;
			for (Iterator it = list.iterator(); it.hasNext(); ++j) {
				final Integer i = (Integer)it.next();
				ary[j] = i != null ? i.intValue(): defaultValue;
			}
		} else {
			ary = null;
		}
		return ary;
	}
	/** Converts an array of numbers to a string.
	 */
	/*package*/ static final String numbersToString(int[] ary) {
		if (ary == null || ary.length == 0)
			return "";

		final StringBuffer sb = new StringBuffer(50);
		for (int j = 0;;) {
			sb.append(ary[j]);
			if (++j >= ary.length) {
				sb.append(',');
				break;
			}
		}
		return sb.toString();
	}

	/** Returns the HTML attributes for the child of the specified index.
	 */
	public String getChildAttrs(int index) {
		int realIndex = index, span = 1;
		if (_spans != null) {
			for (int j = 0; j < _spans.length; ++j) {
				if (j == index) {
					span = _spans[j];
					break;
				}
				realIndex += _spans[j] - 1;
			}
		}

		//Note: row's sclass, if any, becomes TD's sclass. Otherwise,
		//grid.getSclass() + "ev" is used
		String sclass = getSclass();
		String colattrs = null, wd = null, hgh = null;
		final Grid grid = getGrid();
		if (grid != null) {
			if (sclass == null || sclass.length() == 0) {
				sclass = grid.getSclass();
				if (sclass == null || sclass.length() == 0)
					sclass = "gridev";
				else
					sclass += "ev";
			}

			final Columns cols = grid.getColumns();
			if (cols != null) {
				final List colchds = cols.getChildren();
				if (realIndex < colchds.size()) {
					final Column col = (Column)colchds.get(realIndex);
					colattrs = col.getColAttrs();
					if (span == 1) wd = col.getWidth();
						//Bug 1633982: don't generate width if span > 1
						//Side effect: the width might not be the same as specified
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

		if (colattrs == null && sclass == null && style.length() == 0
		&& span == 1)
			return "";

		final StringBuffer sb = new StringBuffer(100);
		if (colattrs != null)
			sb.append(colattrs);
		if (span > 1)
			sb.append(" colspan=\"").append(span).append('"');
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
			.append("!chdextr\"");

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
