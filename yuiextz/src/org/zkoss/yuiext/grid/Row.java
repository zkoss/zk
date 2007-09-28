/* Row.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jun 22, 2007 3:09:53 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkforge.yuiext.grid;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.zkoss.lang.JVMs;
import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.client.Updatable;
import org.zkforge.yuiext.grid.Column;
import org.zkforge.yuiext.grid.Columns;
import org.zkforge.yuiext.grid.Grid;
import org.zkforge.yuiext.grid.Rows;
import org.zkoss.zul.impl.XulElement;

/**
 * A single row in a {@link Rows} element. Each child of the {@link Row} element
 * is placed in each successive cell of the grid. The row with the most child
 * elements determines the number of columns in each row.
 * 
 * @author jumperchen
 * 
 */
public class Row extends XulElement {
	/** disable smartUpdate; usually caused by the client. */
	private boolean _noSmartUpdate;

	private Object _value;

	private String _align, _valign;

	private boolean _selected;

	/** Used only to generate {@link #getChildAttrs}. */
	private transient int _rsflags;

	private boolean _nowrap;

	/**
	 * whether the content of this row is loaded; used if the grid owning this
	 * row is using a list model.
	 */
	private boolean _loaded;

	/** Returns the grid that contains this row. */
	public Grid getGrid() {
		final Component parent = getParent();
		return parent != null ? (Grid) parent.getParent() : null;
	}

	/**
	 * Returns the horizontal alignment of the whole row.
	 * <p>
	 * Default: null (system default: left unless CSS specified).
	 */
	public String getAlign() {
		return _align;
	}

	/**
	 * Sets the horizontal alignment of the whole row.
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}

	/**
	 * Returns the nowrap.
	 * <p>
	 * Default: null (system default: wrap).
	 */
	public boolean isNowrap() {
		return _nowrap;
	}

	/**
	 * Sets the nowrap.
	 */
	public void setNowrap(boolean nowrap) {
		if (_nowrap != nowrap) {
			_nowrap = nowrap;
			smartUpdate("nowrap", _nowrap);
		}
	}

	/**
	 * Returns the vertical alignment of the whole row.
	 * <p>
	 * Default: null (system default: top).
	 */
	public String getValign() {
		return _valign;
	}

	/**
	 * Sets the vertical alignment of the whole row.
	 */
	public void setValign(String valign) {
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdate("valign", _valign);
		}
	}

	/**
	 * Returns the value.
	 * <p>
	 * Default: null.
	 * <p>
	 * Note: the value is application dependent, you can place whatever value
	 * you want.
	 */
	public Object getValue() {
		return _value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value.
	 *            <p>
	 *            Note: the value is application dependent, you can place
	 *            whatever value you want.
	 */
	public void setValue(Object value) {
		_value = value;
	}

	/**
	 * Returns whether it is selected.
	 * <p>
	 * Default: false.
	 */
	public boolean isSelected() {
		return _selected;
	}

	public Rows getRows() {
		return (Rows) getParent();
	}

	/**
	 * Sets whether it is selected.
	 */
	public void setSelected(boolean selected) {
		if (_selected != selected) {
			final Grid grid = getRows().getGrid();
			if (grid != null) {
				// Note: we don't update it here but let its parent does the job
				grid.toggleItemSelection(this);
			} else {
				_selected = selected;
			}
		}
	}

	/* package */final void setSelectedDirectly(boolean selected) {
		_selected = selected;
	}

	/**
	 * Sets whether the content of this row is loaded; used if the grid owning
	 * this row is using a list model.
	 */
	/* package */final void setLoaded(boolean loaded) {
		if (loaded != _loaded) {
			_loaded = loaded;

			final Grid grid = getGrid();
			if (grid != null && grid.getModel() != null)
				smartUpdate("z.loaded", _loaded);
		}
	}

	/**
	 * Returns whether the content of this row is loaded; used if the grid
	 * owning this row is using a list model.
	 */
	/* package */final boolean isLoaded() {
		return _loaded;
	}

	/**
	 * Returns the index of the specified row. The current implementation is
	 * stupid, so not public it yet.
	 */
	/* package */int getIndex() {
		return getParent().getChildren().indexOf(this);
	}

	/**
	 * Returns the HTML attributes for the child of the specified index.
	 */
	public String getChildAttrs(int index) {
		int realIndex = index, span = 1;

		String colattrs = null, wd = null, hgh = null;
		final Grid grid = getGrid();
		if (grid != null) {
			final Columns cols = grid.getColumns();
			if (cols != null) {
				final List colchds = cols.getChildren();
				if (realIndex < colchds.size()) {
					final Column col = (Column) colchds.get(realIndex);
					colattrs = col.getColAttrs();
					if (span == 1)
						wd = col.getWidth();
					// Bug 1633982: don't generate width if span > 1
					// Side effect: the width might not be the same as specified
					hgh = col.getHeight();
				}
			}
		}

		String style;
		_rsflags = RS_NO_WIDTH | RS_NO_HEIGHT | RS_NO_DISPLAY;
		try {
			style = getRealStyle();
		} finally {
			_rsflags = 0;
		}

		if (wd != null || hgh != null) {
			final StringBuffer sb = new StringBuffer(80).append(style);
			// HTMLs.appendStyle(sb, "width", wd); disabled for resize fuction.
			HTMLs.appendStyle(sb, "height", hgh);
			style = sb.toString();
		}

		if (colattrs == null && style.length() == 0 && span == 1)
			return "";

		final StringBuffer sb = new StringBuffer(100);
		if (colattrs != null)
			sb.append(colattrs);

		HTMLs.appendAttribute(sb, "style", style);

		return sb.toString();
	}

	// -- super --//
	protected int getRealStyleFlags() {
		return super.getRealStyleFlags() | _rsflags;
	}

	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		final String clkattrs = getAllOnClickAttrs(false);
		if (clkattrs != null)
			sb.append(clkattrs);
		HTMLs.appendAttribute(sb, "align", _align);
		HTMLs.appendAttribute(sb, "valign", _valign);
		if (_nowrap)
			HTMLs.appendAttribute(sb, "nowrap", "nowrap");

		final Grid grid = getGrid();
		if (grid != null && grid.getModel() != null)
			HTMLs.appendAttribute(sb, "z.loaded", _loaded);
		return sb.toString();
	}

	public void setStyle(String style) {
		if (style != null && style.length() == 0)
			style = null;

		final String s = getStyle();
		if (!Objects.equals(s, style)) {
			super.setStyle(style);
			invalidate(); // yes, invalidate
		}
	}

	public void setSclass(String sclass) {
		if (sclass != null && sclass.length() == 0)
			sclass = null;

		final String s = getSclass();
		if (!Objects.equals(s, sclass)) {
			super.setSclass(sclass);
			invalidate(); // yes, invalidate
		}
	}

	// -- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Rows))
			throw new UiException("Unsupported parent for row: " + parent);
		super.setParent(parent);
	}

	public void onDrawNewChild(Component child, StringBuffer out)
			throws IOException {
		final StringBuffer sb = new StringBuffer(128).append(
				"<td z.type=\"yuiextz.grid.ExtCell\" id=\"").append(
				child.getUuid()).append("!chdextr\"");

		final Grid grid = getGrid();
		if (grid != null) {
			int j = 0;
			for (Iterator it = getChildren().iterator(); it.hasNext(); ++j)
				if (child == it.next())
					break;
			sb.append(getChildAttrs(j));
		}
		sb.append('>');
		if (JVMs.isJava5())
			out.insert(0, sb); // Bug 1682844
		else out.insert(0, sb.toString());
		out.append("</td>");
	}

	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof org.zkforge.yuiext.grid.Label) 
				&& !(child instanceof org.zkforge.yuiext.grid.Image) )
			throw new UiException("Unsupported child for rows: " + child);
		final Grid grid = getGrid();
		if (!getChildren().contains(child)
				&& grid != null
				&& grid.getColumns().getChildren().size() <= getChildren()
						.size())
			throw new UiException(
					"Unsupported the size of Row child more then the size of Column");
		return super.insertBefore(child, insertBefore);
	}

	protected void addMoved(Component oldparent, Page oldpg, Page newpg) {
		if (!_noSmartUpdate) {
			super.addMoved(oldparent, oldpg, newpg);
		}
	}

	/* package */final boolean isSmartUpdate() {
		return !_noSmartUpdate;
	}

	public void redraw(Writer out) throws IOException {
		if (getGrid().getColumns().getChildren().size() < getChildren().size())
			throw new UiException(
					"Unsupported the size of Row child more then the size of Column");
		super.redraw(out);
	}

	// -- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}

	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Updatable {
		public void setResult(Object result) {
			_noSmartUpdate = ((Boolean) result).booleanValue();
		}
	}
}
