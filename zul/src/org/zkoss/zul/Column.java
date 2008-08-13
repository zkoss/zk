/* Column.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:36     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Classes;
import org.zkoss.mesg.Messages;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Namespaces;

import org.zkoss.zul.impl.HeaderElement;
import org.zkoss.zul.mesg.MZul;

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
 * <p>Default {@link #getMoldSclass}: z-grid-column. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Column extends HeaderElement {
	private String _sortDir = "natural";
	private Comparator _sortAsc, _sortDsc;

	public Column() {
		setMoldSclass("z-grid-column");
	}
	public Column(String label) {
		this();
		setLabel(label);
	}
	/* Constructs a grid header with label and image.
	 *
	 * @param lable the label. No label if null or empty.
	 * @param src the URI of the image, or null to ignore.
	 */
	public Column(String label, String src) {
		this();
		setLabel(label);
		setImage(src);
	}
	/* Constructs a grid header with label, image and width.
	 *
	 * @param lable the label. No label if null or empty.
	 * @param src the URI of the image. Ignored if null or empty.
	 * @param width the width of the column. Ignored if null or empty.
	 * @since 3.0.4
	 */
	public Column(String label, String src, String width) {
		this();
		setLabel(label);
		setImage(src);
		setWidth(width);
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
			smartUpdate("z.sort", _sortDir); //don't use null because sel.js assumes it
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
			_sortAsc = sorter;
			invalidate();
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
			_sortDsc = sorter;
			invalidate();
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

		final Page page = getPage();
		final Class cls = page != null ?
			page.getZScriptClass(clsnm): Classes.forNameByThread(clsnm);
		if (cls == null)
			throw new ClassNotFoundException(clsnm);
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
	 * <p>It sorts the rows by use of {@link Components#sort}, if not live
	 * data (i.e., {@link Grid#getModel} is null).
	 *
	 * <p>On the other hand, it invokes {@link ListModelExt#sort} to sort
	 * the rows, if live data (i.e., {@link Grid#getModel} is not null).
	 * In other words, if you use the live data, you have to implement
	 * {@link ListModelExt} to sort the live data explicitly.
	 *
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @return whether the rows are sorted.
	 * @exception UiException if {@link Grid#getModel} is not
	 * null but {@link ListModelExt} is not implemented.
	 */
	public boolean sort(boolean ascending) {
		final String dir = getSortDirection();		
		if (ascending) {
			if ("ascending".equals(dir)) return false;
		} else {
			if ("descending".equals(dir)) return false;
		}

		final Comparator cmpr = ascending ? _sortAsc: _sortDsc;
		if (cmpr == null) return false;

		final Grid grid = getGrid();
		if (grid == null) return false;

		//comparator might be zscript
		final HashMap backup = new HashMap();
		final Namespace ns = Namespaces.beforeInterpret(backup, this, true);
		try {
			final ListModel model = grid.getModel();
			if (model != null) { //live data
				if (!(model instanceof ListModelExt))
					throw new UiException("ListModelExt must be implemented in "+model.getClass().getName());
				((ListModelExt)model).sort(cmpr, ascending);
			} else { //not live data
				final Rows rows = grid.getRows();
				if (rows.hasGroup())
					for (Iterator it = rows.getGroups().iterator(); it.hasNext();) {
						Group g = (Group)it.next();
						int index = g.getIndex() + 1;
						Components.sort(rows.getChildren(), index, index + g.getItemCount(), cmpr);
					}
				else Components.sort(rows.getChildren(), cmpr);
			}
		} finally {
			Namespaces.afterInterpret(backup, ns, true);
		}
		fixDirection(grid, ascending);
		
		return true;
	}
	
	private void fixDirection(Grid grid, boolean ascending) {
		//maintain
		for (Iterator it = grid.getColumns().getChildren().iterator();
		it.hasNext();) {
			final Column hd = (Column)it.next();
			hd.setSortDirection(
				hd != this ? "natural": ascending ? "ascending": "descending");
		}
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
	/**
	 * Groups the rows ({@link Row}) based on {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * 
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @return whether the rows are grouped.
	 * @since 3.5.0
	 */
	public boolean groupByField(boolean ascending) {
		final String dir = getSortDirection();		
		if (ascending) {
			if ("ascending".equals(dir)) return false;
		} else {
			if ("descending".equals(dir)) return false;
		}
		final Comparator cmpr = ascending ? _sortAsc: _sortDsc;
		if (cmpr == null) return false;
		
		final Grid grid = getGrid();
		if (grid == null) return false;
		
		//comparator might be zscript
		final HashMap backup = new HashMap();
		final Namespace ns = Namespaces.beforeInterpret(backup, this, true);
		try {
			final ListModel model = grid.getModel();
			int index = grid.getColumns().getChildren().indexOf(this);
			if (model != null) { //live data
				if (!(model instanceof GroupModel))
					throw new UiException("GroupModel must be implemented in "+model.getClass().getName());
				((GroupModel)model).groupByField(cmpr, ascending, index);
			} else { // not live data
				final Rows rows = grid.getRows();		
				if (rows.hasGroup()) {
					final List groups = new ArrayList(rows.getGroups());
					for (Iterator it = groups.iterator(); it.hasNext();)
						rows.removeChild((Group)it.next()); // Groupfoot is removed automatically, if any.
				}
				
				Components.sort(rows.getChildren(), cmpr);
				
				final List children = new ArrayList(rows.getChildren());
				rows.getChildren().clear();
				
				Group group = null;
				for (Iterator it = children.iterator(); it.hasNext();) {
					final Row row = (Row) it.next();
					final List child = row.getChildren();
					if (child.size() < index)
						throw new IndexOutOfBoundsException(
								"Index: "+index+", Size: "+ child.size());
					final Component cmp = (Component) child.get(index);
					if (cmp instanceof Label) {
						String val = ((Label)cmp).getValue();
						if (group == null) group = new Group(val);
						else if (!Objects.equals(group.getLabel(), val))
							group = new Group(val);
					} else {
						group = new Group(Messages.get(MZul.GRID_OTHER));
					}
					if (group.getParent() != rows)
						rows.appendChild(group);
					rows.appendChild(row);		    		
				}
			}
		} finally {
			Namespaces.afterInterpret(backup, ns, true);
		}

		fixDirection(grid, ascending);
		return true;
	}
	
	public void setLabel(String label) {
		super.setLabel(label);
		if (getParent() != null)
			((Columns)getParent()).postOnInitLater();
	}
	
	public boolean setVisible(boolean visible) {
		boolean old = super.setVisible(visible);
		if (getParent() != null)
			((Columns)getParent()).postOnInitLater();
		return old;
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

	public String getRealSclass() {
		final String scls = super.getRealSclass();
		final String sort = _sortAsc != null || _sortDsc != null ? "sort": "";
		return scls != null ? scls + ' ' + sort : sort;
	}
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(80);
		if (_sortAsc != null) sb.append(" z.asc=\"true\"");
		if (_sortDsc != null) sb.append(" z.dsc=\"true\"");

		if (!"natural".equals(_sortDir))
			HTMLs.appendAttribute(sb, "z.sort", _sortDir);

		final String clkattrs = getAllOnClickAttrs();
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
