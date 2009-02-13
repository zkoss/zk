/* Row.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:43     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

import org.zkoss.lang.JVMs;
import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.impl.Utils;

/**
 * A single row in a {@link Rows} element.
 * Each child of the {@link Row} element is placed in each successive cell
 * of the grid. The row with the most child elements determines the number
 * of columns in each row.
 *
 * <p>Default {@link #getSclass}: the same as grid's sclass.
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
	/** whether the content of this row is loaded; used if
	 * the grid owning this row is using a list model.
	 */
	private boolean _loaded;

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
		return Utils.intsToString(_spans);
	}
	/** Sets the spans, which is a list of numbers separated by comma.
	 *
	 * <p>For example, "1,2,3" means the second column will span two columns
	 * and the following column span three columns, while others occupies
	 * one column.
	 */
	public void setSpans(String spans) throws WrongValueException {
		final int[] ispans = Utils.stringToInts(spans, 1);
		if (!Objects.equals(ispans, _spans)) {
			_spans = ispans;
			invalidate();
		}
	}


	/** Sets whether the content of this row is loaded; used if
	 * the grid owning this row is using a list model.
	 */
	/*package*/ final void setLoaded(boolean loaded) {
		if (loaded != _loaded) {
			_loaded = loaded;

			final Grid grid = getGrid();
			if (grid != null && grid.getModel() != null)
				if (_loaded && !grid.inPagingMold())
					invalidate();
					//reason: the client doesn't init (for better performance)
					//i.e., z.skipsib is specified for unloaded items
				else
					smartUpdate("z.loaded", _loaded);
		}
	}
	/** Returns whether the content of this row is loaded; used if
	 * the grid owning this row is using a list model.
	 */
	/*package*/ final boolean isLoaded() {
		return _loaded;
	}
	/** Returns the index of the specified row.
	 * The current implementation is stupid, so not public it yet.
	 */
	/*package*/ int getIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator();
		it.hasNext(); ++j) {
			if (it.next() == this)
				break;
		}
		return j;
	}

	protected String getRealSclass() {
		final String sclx = (String) getParent().getAttribute(Attributes.STRIPE_STATE);
		return super.getRealSclass() + (sclx != null ? " " + sclx : "");
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

		String colattrs = null, visible = null, hgh = null;
		final Grid grid = getGrid();
		if (grid != null) {
			final Columns cols = grid.getColumns();
			if (cols != null) {
				final List colchds = cols.getChildren();
				if (realIndex < colchds.size()) {
					final Column col = (Column)colchds.get(realIndex);
					colattrs = col.getColAttrs();
					//if (span == 1) wd = col.getWidth();
						//Bug 1633982: don't generate width if span > 1
						//Side effect: the width might not be the same as specified
					visible = col.isVisible() ? "" : "display:none";
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

		if (visible != null || hgh != null) {
			final StringBuffer sb = new StringBuffer(80).append(style).append(visible);
			HTMLs.appendStyle(sb, "height", hgh);
			style = sb.toString();
		}

		if (colattrs == null && style.length() == 0 && span == 1)
			return " class=\"gc\"";

		final StringBuffer sb = new StringBuffer(100);
		if (colattrs != null)
			sb.append(colattrs);
		if (span != 1)
			sb.append(" colspan=\"").append(span).append('"');
		HTMLs.appendAttribute(sb, "style", style);
		
		return sb.append(" class=\"gc\"").toString();
	}

	//-- super --//
	protected int getRealStyleFlags() {
		return super.getRealStyleFlags() | _rsflags;
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		final String clkattrs = getAllOnClickAttrs();
		if (clkattrs != null)
			sb.append(clkattrs);

		HTMLs.appendAttribute(sb, "z.rid", getGrid().getUuid());
		HTMLs.appendAttribute(sb, "align", _align);
		HTMLs.appendAttribute(sb, "valign", _valign);
		if (_nowrap)
			HTMLs.appendAttribute(sb, "nowrap", "nowrap");

		final Grid grid = getGrid();
		if (grid != null && grid.getModel() != null) {
			HTMLs.appendAttribute(sb, "z.loaded", _loaded);

			if (_loaded && !grid.inPagingMold()) {
				final Component c = getNextSibling();
				if (c instanceof Row && !((Row)c)._loaded)
					HTMLs.appendAttribute(sb, "z.skipsib", "true");
			}
		}
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
	/** Returns the style class.
	 * By default, it is the same as grid's stye class, unless
	 * {@link #setSclass} is called with non-empty value.
	 */
	public String getSclass() {
		final String sclass = super.getSclass();
		if (sclass != null) return sclass;

		final Grid grid = getGrid();
		return grid != null ? grid.getSclass(): sclass;
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
			.append("<td z.type=\"Gcl\" id=\"").append(child.getUuid())
			.append("!chdextr\"");

		final Grid grid = getGrid();
		if (grid != null) {
			int j = 0;
			for (Iterator it = getChildren().iterator(); it.hasNext(); ++j)
				if (child == it.next())
					break;
			sb.append(getChildAttrs(j));
		}
		sb.append("><div id=\"").append(child.getUuid())
		.append("!cell\"").append(" class=\"gc cell-inner\">");
		if (JVMs.isJava5()) out.insert(0, sb); //Bug 1682844
		else out.insert(0, sb.toString());
		out.append("</div></td>");
	}
}
