/* Column.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:36     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;
import java.util.Comparator;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.impl.HeaderElement;

/**
 * A single column in a {@link Columns} element.
 * Each child of the {@link Column} element is placed in each successive
 * cell of the grid.
 * The column with the most child elements determines the number of rows
 * in each column.
 *
 * <p>The use of column is mainly to define attributes for each cell
 * in the grid.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Column extends HeaderElement {
	private String _sortDir = "natural";
	private Comparator _sortAsc, _sortDsc;

	public Column() {
	}
	public Column(String label) {
		setLabel(label);
	}
	public Column(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/** Returns the grid that contains this column. */
	public Grid getGrid() {
		final Component parent = getParent();
		return parent != null ? (Grid)parent.getParent(): null;
	}

	/** Returns the sort direction.
	 * <p>Default: "natural".
	 */
	public String getSortDirection() {
		return _sortDir;
	}
	/** Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the grid is sorted.
	 *
	 * <p>If you use {@link #sort(boolean)} to sort rows ({@link Row}),
	 * the sort direction is maintained automatically.
	 * If you want to sort it in customized way, you have to set the
	 * sort direction manaully.
	 *
	 * @param sortDir one of "ascending", "descending" and "natural"
	 */
	public void setSortDirection(String sortDir) throws WrongValueException {
		if (sortDir == null || (!"ascending".equals(sortDir)
		&& !"descending".equals(sortDir) && !"natural".equals(sortDir)))
			throw new WrongValueException("Unknown sort direction: "+sortDir);
		if (!Objects.equals(_sortDir, sortDir)) {
			_sortDir = sortDir;
			smartUpdate("zk_sort", _sortDir); //don't use null because sel.js assumes it
		}
	}

	/** Returns the ascending sorter, or null if not available.
	 */
	public Comparator getSortAscending() {
		return _sortAsc;
	}
	/** Sets the ascending sorter, or null for no sorter for
	 * the ascending order.
	 */
	public void setSortAscending(Comparator sorter) {
		if (!Objects.equals(_sortAsc, sorter)) {
			if (sorter == null) smartUpdate("zk_asc", null);
			else if (_sortAsc == null) smartUpdate("zk_asc", "true");
			_sortAsc = sorter;
		}
	}
	/** Sets the ascending sorter with the class name, or null for
	 * no sorter for the ascending order.
	 */
	public void setSortAscending(String clsnm)
	throws ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		setSortAscending(toComparator(clsnm));
	}

	/** Returns the descending sorter, or null if not available.
	 */
	public Comparator getSortDescending() {
		return _sortDsc;
	}
	/** Sets the descending sorter, or null for no sorter for the
	 * descending order.
	 */
	public void setSortDescending(Comparator sorter) {
		if (!Objects.equals(_sortDsc, sorter)) {
			if (sorter == null) smartUpdate("zk_dsc", null);
			else if (_sortDsc == null) smartUpdate("zk_dsc", "true");
			_sortDsc = sorter;
		}
	}
	/** Sets the descending sorter with the class name, or null for
	 * no sorter for the descending order.
	 */
	public void setSortDescending(String clsnm)
	throws ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		setSortDescending(toComparator(clsnm));
	}

	private Comparator toComparator(String clsnm)
	throws ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		if (clsnm == null || clsnm.length() == 0) return null;

		final Class cls = getClass(clsnm);
		if (!Comparator.class.isAssignableFrom(cls))
			throw new UiException("Comparator must be implemented: "+clsnm);
		return (Comparator)cls.newInstance();
	}

	/** Sorts the rows ({@link Row}) based on {@link #getSortAscending}
	 * and {@link #getSortDescending}, if {@link #getSortDirection} doesn't
	 * matches the ascending argument.
	 *
	 * <p>It checks {@link #getSortDirection} to see whether sorting
	 * is required, and update {@link #setSortDirection} after sorted.
	 * For example, if {@link #getSortDirection} returns "ascending" and
	 * the ascending argument is false, nothing happens.
	 * To enforce the sorting, you can invoke {@link #setSortDirection}
	 * with "natural" before invoking this method.
	 * Alternatively, you can invoke {@link #sort(boolean, boolean)} instead.
	 *
	 * <p>It sorts the rows by use of {@link Components#sort}.
	 *
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @return whether the rows are sorted.
	 */
	public boolean sort(boolean ascending) {
		final String dir = getSortDirection();
		if (ascending) {
			if ("ascending".equals(dir)) return false;
		} else {
			if ("descending".equals(dir)) return false;
		}

		final Comparator cpr = ascending ? _sortAsc: _sortDsc;
		if (cpr == null) return false;

		final Grid grid = getGrid();
		if (grid == null) return false;
		Components.sort(grid.getRows().getChildren(), cpr);

		//maintain
		for (Iterator it = grid.getColumns().getChildren().iterator();
		it.hasNext();) {
			final Column hd = (Column)it.next();
			hd.setSortDirection(
				hd != this ? "natural": ascending ? "ascending": "descending");
		}
		return true;
	}
	/** Sorts the rows ({@link Row}) based on {@link #getSortAscending}
	 * and {@link #getSortDescending}.
	 *
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @param force whether to enforce the sorting no matter what the sort
	 * direction ({@link #getSortDirection}) is.
	 * If false, this method is the same as {@link #sort(boolean)}.
	 * @return whether the rows are sorted.
	 */
	public boolean sort(boolean ascending, boolean force) {
		if (force) setSortDirection("natural");
		return sort(ascending);
	}

	//-- event listener --//
	/** It invokes {@link #sort(boolean)} to sort list items and maintain
	 * {@link #getSortDirection}.
	 */
	public void onSort() {
		final String dir = getSortDirection();
		if ("ascending".equals(dir)) sort(false);
		else if ("descending".equals(dir)) sort(true);
		else if (!sort(true)) sort(false);
	}

	/** Returns the style class.
	 * If the style class is not defined ({@link #setSclass} is not called
	 * or called with null or empty), it returns "sort" if sortable,
	 * or null if not sortable.
	 * <p>By sortable we mean that {@link #setSortAscending}
	 * or {@link #setSortDescending}
	 * was called with a non-null comparator
	 */
	public String getSclass() {
		final String scls = super.getSclass();
		if (scls != null) return scls;
		return _sortAsc != null || _sortDsc != null ? "sort": null;
	}

	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(80);
		if (_sortAsc != null) sb.append(" zk_asc=\"true\"");
		if (_sortDsc != null) sb.append(" zk_dsc=\"true\"");

		if (!"natural".equals(_sortDir))
			HTMLs.appendAttribute(sb, "zk_sort", _sortDir);

		final String clkattrs = getAllOnClickAttrs(false);
		if (clkattrs != null) sb.append(clkattrs);

		final String attrs = super.getOuterAttrs();
		if (sb.length() == 0) return attrs;
		return sb.insert(0, attrs).toString();
	}

	/** Invalidates the whole grid. */
	protected void invalidateWhole() {
		final Grid grid = getGrid();
		if (grid != null) grid.invalidate();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Columns))
			throw new UiException("Unsupported parent for column: "+parent);
		super.setParent(parent);
	}
}
