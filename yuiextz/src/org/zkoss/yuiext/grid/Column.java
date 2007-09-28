/* Column.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jun 22, 2007 3:01:59 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext.grid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.util.Locales;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Namespaces;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelExt;
import org.zkoss.zul.ListitemComparator;
import org.zkoss.zul.impl.HeaderElement;
import org.zkoss.yuiext.grid.Columns;

/**
 * A single column in a {@link Columns} element. Each child of the
 * {@link Column} element is placed in each successive cell of the grid. The
 * column with the most child elements determines the number of rows in each
 * column.
 * 
 * <p>
 * The use of column is mainly to define attributes for each cell in the grid.
 * 
 * @author jumperchen
 * 
 */
public class Column extends HeaderElement {
	private String _sortDir = "natural";
	private Comparator _sortAsc, _sortDsc;
	/**
	 * This type for string editing.
	 */
	public static final String EDITOR_TYPE_TEXTFIELD = "text";

	/**
	 * This type for drop-down list.
	 */
	public static final String EDITOR_TYPE_COMBOBOX = "combo";

	/**
	 * This type for a numeric.
	 */
	public static final String EDITOR_TYPE_NUMBERFIELD = "number";

	/**
	 * This type for date format.
	 */
	public static final String EDITOR_TYPE_DATEFIELD = "date";

	/**
	 * This type for check box.
	 */
	public static final String EDITOR_TYPE_CHECKBOX = "check";

	/**
	 * This type for reading only.
	 */
	public static final String EDITOR_TYPE_READONLY = "readonly";

	private boolean _lockable;

	private String _editortype = EDITOR_TYPE_TEXTFIELD;

	private String _format = "yyyy/MM/dd";

	private String _combo;

	public Column() {
	}

	public Column(String label) {
		setLabel(label);
	}

	public Column(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/**
	 * Sets the column editor type. If this grid is not editable, the type will
	 * be ignored.
	 * 
	 * @param type
	 *            editor type.
	 * @see #EDITOR_TYPE_TEXTFIELD
	 * @see #EDITOR_TYPE_COMBOBOX
	 * @see #EDITOR_TYPE_NUMBERFIELD
	 * @see #EDITOR_TYPE_DATEFIELD
	 * @see #EDITOR_TYPE_CHECKBOX
	 */
	public void setEditortype(String type) {
		if (!Objects.equals(_editortype, type)) {
			if (!EDITOR_TYPE_TEXTFIELD.equals(type)
					&& !EDITOR_TYPE_COMBOBOX.equals(type)
					&& !EDITOR_TYPE_NUMBERFIELD.equals(type)
					&& !EDITOR_TYPE_DATEFIELD.equals(type)
					&& !EDITOR_TYPE_CHECKBOX.equals(type)
					&& !EDITOR_TYPE_READONLY.equals(type))
				throw new UiException("Unsupported type: " + type);
			_editortype = type;
			invalidateWhole();
		}
	}

	/**
	 * Sets the format.
	 */
	public void setFormat(String format) {
		if (!EDITOR_TYPE_DATEFIELD.equals(getEditortype()))
			throw new UiException("Appliable only to the datetype: " + this);
		if (format == null || format.length() == 0)
			format = getDefaultFormat();
		if (!Objects.equals(_format, format)) {
			_format = format;
			invalidateWhole();
		}
	}

	/**
	 * Returns the format.
	 * <p>
	 * Default: 'yyyy/MM/dd' (used what is defined in the format sheet).
	 */
	public String getFormat() {
		return _format;
	}

	/**
	 * Returns the ID of {@link org.zkoss.yuiext.grid.Listbox} that should
	 * appear when the user clicks on the element.
	 * 
	 * <p>
	 * Default: null (no combo).
	 */
	public String getCombo() {
		return _combo;
	}

	/**
	 * Sets the ID of {@link org.zkoss.yuiext.grid.Listbox} that should appear
	 * when the user clicks on the element.
	 */
	public void setCombo(String combo) {
		if (!EDITOR_TYPE_COMBOBOX.equals(getEditortype()))
			throw new UiException("Appliable only to the combotype: " + this);
		if (!Objects.equals(_combo, combo)) {
			_combo = combo;
			invalidateWhole();
		}
	}

	/**
	 * Returns the default format, which is used when contructing a date type
	 * for editor.
	 * <p>
	 * The default format ({@link #getFormat}) depends on JVM's setting and
	 * the current user's locale. That is,
	 * <code>DateFormat.getDateInstance(DateFormat,DEFAULT, Locales.getCurrent).</code>
	 * 
	 * <p>
	 * You might override this method to provide your own default format.
	 */
	protected String getDefaultFormat() {
		final DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,
				Locales.getCurrent());
		if (df instanceof SimpleDateFormat) {
			final String fmt = ((SimpleDateFormat) df).toPattern();
			if (fmt != null && !"M/d/yy h:mm a".equals(fmt))
				return fmt; // note: JVM use "M/d/yy h:mm a" if not found!
		}
		return "yyyy/MM/dd";
	}

	/**
	 * Returns the column editor type.
	 */
	public String getEditortype() {
		return _editortype;
	}

	/**
	 * Sets whether this column is lockable.
	 */
	public void setLockable(boolean lockable) {
		Grid grid = getGrid();
		if (grid != null && grid.isEditable())
			throw new UiException(
					"Unsupported condition: The grid is editable.");
		if (_lockable != lockable) {
			_lockable = lockable;
			smartUpdate("z.lock", _lockable);
		}
	}

	/**
	 * Returns true if this column is lockable.
	 */
	public boolean isLockable() {
		return _lockable;
	}

	/** Returns the grid that contains this column. */
	public Grid getGrid() {
		final Component parent = getParent();
		return parent != null ? (Grid) parent.getParent() : null;
	}

	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(80);
		final String clkattrs = getAllOnClickAttrs(false);
		if (clkattrs != null)
			sb.append(clkattrs);
		final String attrs = super.getOuterAttrs();
		if (_sortAsc != null) sb.append(" z.asc=\"true\"");
		if (_sortDsc != null) sb.append(" z.dsc=\"true\"");

		if (!"natural".equals(_sortDir))
			HTMLs.appendAttribute(sb, "z.sort", _sortDir);
		HTMLs.appendAttribute(sb, "z.lock", String.valueOf(_lockable));
		HTMLs.appendAttribute(sb, "z.editortype", getEditortype());
		HTMLs.appendAttribute(sb, "z.format", getFormat());
		HTMLs.appendAttribute(sb, "z.combo", getCombo());
		if (sb.length() == 0)
			return attrs;
		return sb.insert(0, attrs).toString();
	}

	/** Invalidates the whole grid. */
	protected void invalidateWhole() {
		final Grid grid = getGrid();
		if (grid != null)
			grid.invalidate();
	}

	protected void addMoved(Component oldparent, Page oldpg, Page newpg) {
		if (getParent() != null && getParent() instanceof Row) {
			if (((Row) getParent()).isSmartUpdate()) {
				super.addMoved(oldparent, oldpg, newpg);
			}
		}
	}
	/** Sets the type of the sorter.
	 * You might specify either "auto" or "none".
	 *
	 * <p>If "auto" is specified, it will call
	 * {@link #setSortAscending} and/or {@link #setSortDescending} 
	 * are called with {@link ListitemComparator}, if
	 * {@link #getSortDescending} and/or {@link #getSortAscending} are null.
	 * If you assigned a comparator to them, it won't be affected.
	 * The auto created comparator is case-insensitive.
	 *
	 * <p>If "none" is specified, both {@link #setSortAscending} and
	 * {@link #setSortDescending} are called with null.
	 * Therefore, no more sorting is available to users for this column.
	 */
	public void setSort(String type) {
		if ("auto".equals(type)) {
			if (getSortAscending() == null)
				setSortAscending(new RowComparator(this, true, true, true));
			if (getSortDescending() == null)
				setSortDescending(new RowComparator(this, false, true, true));
		} else if ("none".equals(type)) {
			setSortAscending((Comparator)null);
			setSortDescending((Comparator)null);
		}
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
			if (sorter == null) smartUpdate("z.asc", null);
			else if (_sortAsc == null) smartUpdate("z.asc", "true");
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
			if (sorter == null) smartUpdate("z.dsc", null);
			else if (_sortDsc == null) smartUpdate("z.dsc", "true");
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
				Components.sort(grid.getRows().getChildren(), cmpr);
			}
		} finally {
			Namespaces.afterInterpret(backup, ns, true);
		}

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
	public void onSort(Event event) {
		String dir = (String)event.getData();
		if(dir == null) dir = getSortDirection();			
		if ("ascending".equals(dir)) sort(false);
		else if ("descending".equals(dir)) sort(true);
		else if (!sort(true)) sort(false);
	}

	// -- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Columns))
			throw new UiException("Unsupported parent for column: " + parent);
		super.setParent(parent);
	}

	/* package */final void setLockableDirectly(boolean lockable) {
		_lockable = lockable;
	}
	
	//Cloneable//
	public Object clone() {
		final Column clone = (Column)super.clone();
		clone.fixClone();
		return clone;
	}
	private void fixClone() {
		if (_sortAsc instanceof RowComparator) {
			final RowComparator c = (RowComparator)_sortAsc;
			if (c.getColumn() == this && c.isAscending())
				_sortAsc =
					new RowComparator(this, true, c.shallIgnoreCase());
		}
		if (_sortDsc instanceof RowComparator) {
			final RowComparator c = (RowComparator)_sortDsc;
			if (c.getColumn() == this && !c.isAscending())
				_sortDsc =
					new RowComparator(this, false, c.shallIgnoreCase());
		}
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		boolean written = false;
		if (_sortAsc instanceof RowComparator) {
			final RowComparator c = (RowComparator)_sortAsc;
			if (c.getColumn() == this && c.isAscending()) {
				s.writeBoolean(true);
				s.writeBoolean(c.shallIgnoreCase());
				s.writeBoolean(c.byNativeValue());
				written = true;
			}
		}
		if (!written) {
			s.writeBoolean(false);
			s.writeObject(_sortAsc);
		}

		written = false;
		if (_sortDsc instanceof RowComparator) {
			final RowComparator c = (RowComparator)_sortDsc;
			if (c.getColumn() == this && !c.isAscending()) {
				s.writeBoolean(true);
				s.writeBoolean(c.shallIgnoreCase());
				s.writeBoolean(c.byNativeValue());
				written = true;
			}
		}
		if (!written) {
			s.writeBoolean(false);
			s.writeObject(_sortDsc);
		}
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		boolean b = s.readBoolean();
		if (b) {
			final boolean igcs = s.readBoolean();
			final boolean byval = s.readBoolean();
			_sortAsc = new RowComparator(this, true, igcs, byval);
		} else {
			_sortAsc = (ListitemComparator)s.readObject();
		}

		b = s.readBoolean();
		if (b) {
			final boolean igcs = s.readBoolean();
			final boolean byval = s.readBoolean();
			_sortDsc = new RowComparator(this, false, igcs, byval);
		} else {
			_sortDsc = (ListitemComparator)s.readObject();
		}
	}

}
