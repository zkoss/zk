/* Grid.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 15:40:35     2005, Created by tomyeh@potix.com
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

import com.potix.lang.Objects;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.impl.XulElement;
import com.potix.zul.html.ext.Paginal;

/**
 * A grid is an element that contains both rows and columns elements.
 * It is used to create a grid of elements.
 * Both the rows and columns are displayed at once although only one will
 * typically contain content, while the other may provide size information.
 *
 * <p>There are two ways to handle long content: scrolling and paging.
 * If {@link #getMold} is "default", scrolling is used if {@link #setHeight}
 * is called and too much content to display.
 * If {@link #getMold} is "paging", paging is used if two or more pages are
 * required. To control the number of items to display in a page, use
 * {@link #setPageSize}.
 *
 * <p>If paging is used, the page controller is either created automatically
 * or assigned explicity by {@link #setPaginal}.
 * The paging controller specified explicitly by {@link #setPaginal} is called
 * the external page controller. It is useful if you want to put the paging
 * controller at different location (other than as a child component), or
 * you want to use the same controller to control multiple grids.
 *
 * <p>Default {@link #getSclass}: grid.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Grid extends XulElement {
	private transient Rows _rows;
	private transient Columns _cols;
	private String _align;
	/** The paging controller, used only if mold = "paging". */
	private transient Paginal _pgi;
	/** The paging controller, used only if mold = "paging" and user
	 * doesn't assign a controller via {@link #setPaginal}.
	 */
	private transient Paging _paging;

	public Grid() {
		setSclass("grid");
	}

	/** Returns the rows.
	 */
	public Rows getRows() {
		return _rows;
	}
	/** Returns the columns.
	 */
	public Columns getColumns() {
		return _cols;
	}

	/** Returns the specified cell, or null if not available.
	 * @param row which row to fetch (starting at 0).
	 * @param col which column to fetch (starting at 0).
	 */
	public Component getCell(int row, int col) {
		final Rows rows = getRows();
		if (rows == null) return null;

		List children = rows.getChildren();
		if (children.size() <= row) return null;

		children = ((Row)children.get(row)).getChildren();
		return children.size() <= col ? null: (Component)children.get(col);
	}

	/** Returns the horizontal alignment of the whole grid.
	 * <p>Default: null (system default: left unless CSS specified).
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the horizontal alignment of the whole grid.
	 * <p>Allowed: "left", "center", "right"
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}

	/** Re-initialize the listbox at the client (actually, re-calculate
	 * the column width at the client).
	 */
	/*package*/ void initAtClient() {
		smartUpdate("zk_init", true);
	}

	//--Paging--//
	/** Returns the paging controller, or null if not available.
	 * Note: the paging controller is used only if {@link #getMold} is "paging".
	 *
	 * <p>If mold is "paging", this method never returns null, because
	 * a child paging controller is created automcatically (if not specified
	 * by developers with {@link #setPaginal}).
	 *
	 * <p>If a paging controller is specified (either by {@link #setPaginal},
	 * or by {@link #setMold} with "paging"),
	 * the grid will rely on the paging controller to handle long-content
	 * instead of scrolling.
	 */
	public Paginal getPaginal() {
		return _pgi;
	}
	/* Specifies the paging controller.
	 * Note: the paging controller is used only if {@link #getMold} is "paging".
	 *
	 * <p>It is OK, though without any effect, to specify a paging controller
	 * even if mold is not "paging".
	 *
	 * @param pgi the paging controller. If null and {@link #getMold} is "paging",
	 * a paging controller is created automatically as a child component
	 * (see {@link #getPaging}).
	 */
	public void setPaginal(Paginal pgi) {
		if (!Objects.equals(pgi, _pgi)) {
			_pgi = pgi;
			updateChildPaging();
		}
	}
	/** Creates or detaches the child paging controller automatically.
	 */
	private void updateChildPaging() {
		if ("paging".equals(getMold())) {
			if (_pgi == null) {
				if (_paging != null) _pgi = _paging;
				else {
					 final Paging paging = new Paging();
					 paging.setAutohide(true);
					 paging.setDetailed(true);
					 final int sz = _rows != null ? _rows.getChildren().size(): 0;
					 paging.setTotalSize(sz);
					 paging.setParent(this);
				}
			}
		} else {
			if (_pgi != null && _pgi == _paging)
				_paging.detach();
		}
	}
	/** Returns the child paging controller that is created automatically,
	 * or null if mold is not "paging", or the controller is specified externally
	 * by {@link #setPaginal}.
	 */
	public Paging getPaging() {
		return _paging;
	}
	/** Returns the page size, aka., the number items per page.
	 * @exception IllegalStateException if {@link #getPaginal} returns null,
	 * i.e., mold is not "paging" and no external controller is specified.
	 */
	public int getPageSize() {
		if (_pgi == null)
			throw new IllegalStateException("Available only the paging mold");
		return _pgi.getPageSize();
	}
	/** Sets the page size, aka., the number items per page.
	 * @exception IllegalStateException if {@link #getPaginal} returns null,
	 * i.e., mold is not "paging" and no external controller is specified.
	 */
	public void setPageSize(int pgsz) {
		if (_pgi == null)
			throw new IllegalStateException("Available only the paging mold");
		_pgi.setPageSize(pgsz);
	}

	//-- super --//
	public void setMold(String mold) {
		super.setMold(mold);
		updateChildPaging();
	}
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		return _align != null ? attrs + " align=\"" + _align +'"': attrs;
	}

	//-- Component --//
	public void invalidate(Range range) {
		super.invalidate(range);
		if (range != OUTER) initAtClient();
			//We have to re-init because inner tags are changed
			//If OUTER, init is invoked automatically by client
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (child instanceof Rows) {
			if (_rows != null && _rows != child)
				throw new UiException("Only one rows child is allowed: "+this);
			_rows = (Rows)child;
		} else if (child instanceof Columns) {
			if (_cols != null && _cols != child)
				throw new UiException("Only one columns child is allowed: "+this);
			_cols = (Columns)child;
		} else if (child instanceof Paging) {
			if (_pgi != null)
				throw new UiException("External paging cannot coexist with child paging");
			if (_paging != null && _paging != child)
				throw new UiException("Only one paging is allowed: "+this);
			_pgi = _paging = (Paging)child;
			if (!getChildren().isEmpty())
				insertBefore = (Component)getChildren().get(0); //as the first child
		} else {
			throw new UiException("Unsupported child for grid: "+child);
		}
 
		if (super.insertBefore(child, insertBefore)) {
			invalidate(INNER);
			return true;
		}
		return false;
	}
	public boolean removeChild(Component child) {
		if (!super.removeChild(child))
			return false;

		if (_rows == child) _rows = null;
		else if (_cols == child) _cols = null;
		else if (_paging == child) {
			_paging = null;
			if (_pgi == child) _pgi = null;
		}
		invalidate(INNER);
		return true;
	}

	//Cloneable//
	public Object clone() {
		final Grid clone = (Grid)super.clone();

		int cnt = 0;
		if (clone._rows != null) ++cnt;
		if (clone._cols != null) ++cnt;
		if (clone._paging != null) ++cnt;
		if (cnt > 0) clone.afterUnmarshal(cnt);

		return clone;
	}
	/** @param cnt # of children that need special handling (used for optimization).
	 * -1 means process all of them
	 */
	private void afterUnmarshal(int cnt) {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Rows) {
				_rows = (Rows)child;
				if (--cnt == 0) break;
			} else if (child instanceof Columns) {
				_cols = (Columns)child;
				if (--cnt == 0) break;
			} else if (child instanceof Paging) {
				_pgi = _paging = (Paging)child;
				if (--cnt == 0) break;
			}
		}
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		afterUnmarshal(-1);
		//TODO: how to marshal _pgi if _pgi != _paging
	}
}
