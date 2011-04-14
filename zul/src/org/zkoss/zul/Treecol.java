/* Treecol.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:55:59     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Comparator;
import java.util.Iterator;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Scopes;

import org.zkoss.zul.impl.HeaderElement;

/**
 * A treecol.
 * <p>Default {@link #getZclass}: z-treecol (since 5.0.0)
 * @author tomyeh
 */
public class Treecol extends HeaderElement implements org.zkoss.zul.api.Treecol {
	private static final long serialVersionUID = 20110131122933L;
	
	private String _sortDir = "natural";
	private transient Comparator _sortAsc, _sortDsc;
	private String _sortAscNm = "none";
	private String _sortDscNm = "none";
	private int _maxlength;
	private boolean _ignoreSort = false;

	static {
		addClientEvent(Treecol.class, Events.ON_SORT, CE_DUPLICATE_IGNORE);
	}
	
	public Treecol() {
	}
	public Treecol(String label) {
		super(label);
	}
	/* Constructs a tree header with label and image.
	 *
	 * @param lable the label. No label if null or empty.
	 * @param src the URI of the image. Ignored if null or empty.
	 */
	public Treecol(String label, String src) {
		super(label, src);
	}
	/* Constructs a tree header with label, image and width.
	 *
	 * @param src the URI of the image. Ignored if null or empty.
	 * @param width the width of the column. Ignored if null or empty.
	 * @since 3.0.4
	 */
	public Treecol(String label, String src, String width) {
		super(label, src);
		setWidth(width);
	}

	/** Returns the tree that it belongs to.
	 */
	public Tree getTree() {
		final Component comp = getParent();
		return comp != null ? (Tree)comp.getParent(): null;
	}
	/** Returns the tree that it belongs to.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tree getTreeApi() {
		return getTree();
	}

	/** Returns the sort direction.
	 * @since 5.0.6
	 * <p>Default: "natural".
	 */
	public String getSortDirection() {
		return _sortDir;
	}
	/** Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the tree is sorted. (unless the tree has "autosort" attribute)
	 *
	 * <p>If you use {@link #sort(boolean)} to sort treechildren ({@link Treeitem}),
	 * the sort direction is maintained automatically.
	 * If you want to sort it in customized way, you have to set the
	 * sort direction manually.
	 * @since 5.0.6
	 * @param sortDir one of "ascending", "descending" and "natural"
	 */
	public void setSortDirection(String sortDir) throws WrongValueException {
		if (sortDir == null || (!"ascending".equals(sortDir)
		&& !"descending".equals(sortDir) && !"natural".equals(sortDir)))
			throw new WrongValueException("Unknown sort direction: "+sortDir);
		if (!Objects.equals(_sortDir, sortDir)) {
			_sortDir = sortDir;
			if (!"natural".equals(sortDir) && !_ignoreSort) {
				Tree tree = getTree();
				if (tree != null && tree.isAutosort()) {
					doSort("ascending".equals(sortDir));
				}
			}
			smartUpdate("sortDirection", _sortDir);
		}
	}

	/** Sets the type of the sorter.
	 * You might specify either "auto", "auto(FIELD_NAME1[,FIELD_NAME2] ...)",
	 * "auto(<i>number</i>)" or "none".
	 *
	 * <p>If "client" or "client(number)" is specified,
	 * the sort functionality will be done by Javascript at client without notifying
	 * to server, that is, the order of the component in the row is out of sync.
	 * <ul>
	 * <li> "client" : it is treated by a string</li>
	 * <li> "client(number)" : it is treated by a number</li>
	 * </ul>
	 * <p>Note: client sorting cannot work in model case.
	 * 
	 * <p>If "auto" is specified,
	 * {@link #setSortAscending} and/or {@link #setSortDescending} 
	 * are called with {@link TreeitemComparator}, if
	 * {@link #getSortDescending} and/or {@link #getSortAscending} are null.
	 * If you assigned a comparator to them, it won't be affected.
	 * The auto created comparator is case-insensitive.
	 *
	 * <p>If "auto(FIELD_NAME1, FIELD_NAME2, ...)" is specified,
	 * {@link #setSortAscending} and/or {@link #setSortDescending} 
	 * are called with {@link FieldComparator}, if
	 * {@link #getSortDescending} and/or {@link #getSortAscending} are null.
	 * If you assigned a comparator to them, it won't be affected.
	 * The auto created comparator is case-insensitive.
	 *
	 * <p>If "auto(<i>number</i>)" is specified, 
	 * {@link #setSortAscending} and/or {@link #setSortDescending} 
	 * are called with {@link ArrayComparator}. Notice that the data must
	 * be an array and the number-th element must be comparable ({@link Comparable}).
	 *
	 * <p>If "none" is specified, both {@link #setSortAscending} and
	 * {@link #setSortDescending} are called with null.
	 * Therefore, no more sorting is available to users for this column.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @since 5.0.6
	 */
	public void setSort(String type) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (type == null) return;
		if (type.startsWith("client")) {
			try {
				setSortAscending(type);
				setSortDescending(type);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex); //not possible to throw ClassNotFoundException...
			}
		} else if ("auto".equals(type)) {
			if (getSortAscending() == null)
				setSortAscending(new TreeitemComparator(this, true, true));
			if (getSortDescending() == null)
				setSortDescending(new TreeitemComparator(this, false, true));
		} else if (!Strings.isBlank(type) && type.startsWith("auto")) {
			final int j = type.indexOf('(');
			final int k = type.lastIndexOf(')');
			if (j >= 0 && k >= 0) {
				final String name = type.substring(j+1, k);
				char cc;
				int index = -1;
				if (name.length() > 0 && (cc = name.charAt(0)) >= '0' && cc <= '9')
					if ((index = Integer.parseInt(name)) < 0)
						throw new IllegalArgumentException("Nonnegative number is required: "+name);
				if (getSortAscending() == null)
					if (index < 0)
						setSortAscending(new FieldComparator(name, true));
					else
						setSortAscending(new ArrayComparator(index, true));
				if (getSortDescending() == null)
					if (index < 0)
						setSortDescending(new FieldComparator(name, false));
					else
						setSortDescending(new ArrayComparator(index, false));
			} else {
				throw new UiException("Unknown sort type: "+type);
			}
		} else if ("none".equals(type)) {
			setSortAscending((Comparator)null);
			setSortDescending((Comparator)null);
		}
	}

	/** Returns the ascending sorter, or null if not available.
	 * 	@since 5.0.6
	 */
	public Comparator getSortAscending() {
		return _sortAsc;
	}
	/** Sets the ascending sorter, or null for no sorter for
	 * the ascending order.
	 *
	 * @param sorter the comparator used to sort the ascending order.
	 * @since 5.0.6
	 */
	public void setSortAscending(Comparator sorter) {
		if (!Objects.equals(_sortAsc, sorter)) {
			_sortAsc = sorter;
			String nm = _sortAsc == null ? "none" : "fromServer";
			if (!_sortAscNm.equals(nm)) {
				_sortAscNm = nm;
				smartUpdate("sortAscending", _sortAscNm);
			}
		}
	}
	/** Sets the ascending sorter with the class name, or null for
	 * no sorter for the ascending order.
	 * @since 5.0.6
	 */
	public void setSortAscending(String clsnm)
	throws ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		if (!Strings.isBlank(clsnm) && clsnm.startsWith("client") && !_sortAscNm.equals(clsnm)) {
			_sortAscNm = clsnm;
			smartUpdate("sortAscending", clsnm);
		} else
			setSortAscending(toComparator(clsnm));
	}

	/** Returns the descending sorter, or null if not available.
	 * @since 5.0.6
	 */
	public Comparator getSortDescending() {
		return _sortDsc;
	}
	/** Sets the descending sorter, or null for no sorter for the
	 * descending order.
	 *
	 * @param sorter the comparator used to sort the ascending order.
	 * @since 5.0.6
	 */
	public void setSortDescending(Comparator sorter) {
		if (!Objects.equals(_sortDsc, sorter)) {
			_sortDsc = sorter;
			String nm = _sortDsc == null ? "none" : "fromServer";
			if (!_sortDscNm.equals(nm)) {
				_sortDscNm = nm;
				smartUpdate("sortDescending", _sortDscNm);
			}
		}
	}
	/** Sets the descending sorter with the class name, or null for
	 * no sorter for the descending order.
	 * @since 5.0.6
	 */
	public void setSortDescending(String clsnm)
	throws ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		if (!Strings.isBlank(clsnm) && clsnm.startsWith("client") && !_sortDscNm.equals(clsnm)) {
			_sortDscNm = clsnm;
			smartUpdate("sortDescending", clsnm);
		} else
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

	/** Returns the maximal length of each item's label.
	 * <p>Default: 0 (no limit).
	 */
	public int getMaxlength() {
		return _maxlength;
	}
	/** Sets the maximal length of each item's label.
	 * <p>Default: 0 (no limit).
	 * <p>Notice that maxlength will be applied to this header and all
	 * listcell of the same column.
	 */
	public void setMaxlength(int maxlength) {
		if (maxlength < 0) maxlength = 0;
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			smartUpdate("maxlength", maxlength);
		}
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
	
	/** Sorts the treechildren ({@link Treeitem}) based on {@link #getSortAscending}
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
	 * data (i.e., {@link Tree#getModel} is null).
	 *
	 * <p>On the other hand, it invokes {@link TreeModelExt#sort} to sort
	 * the treechildren, if live data (i.e., {@link Tree#getModel} is not null).
	 * In other words, if you use the live data, you have to implement
	 * {@link TreeModelExt} to sort the live data explicitly.
	 *
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @return whether the rows are sorted.
	 * @exception UiException if {@link Tree#getModel} is not
	 * null but {@link TreeModelExt} is not implemented.
	 * @since 5.0.6
	 */
	public boolean sort(boolean ascending) {
		final String dir = getSortDirection();
		if (ascending) {
			if ("ascending".equals(dir)) return false;
		} else {
			if ("descending".equals(dir)) return false;
		}
		return doSort(ascending);
	}
	
	/*package*/ boolean doSort(boolean ascending) {
		final Comparator cmpr = ascending ? _sortAsc: _sortDsc;
		if (cmpr == null) return false;

		final Tree tree = getTree();
		if (tree == null) return false;

		//comparator might be zscript
		Scopes.beforeInterpret(this);
		try {
			final TreeModel model = tree.getModel();
			boolean isPagingMold = tree.inPagingMold();
			int activePg = isPagingMold ? tree.getPaginal().getActivePage() : 0;
			if (model != null) { //live data
				if (!(model instanceof TreeModelExt))
					throw new UiException("TreeModelExt must be implemented in "+model.getClass().getName());
				((TreeModelExt)model).sort(cmpr, ascending);
			} else { //not live data
				sort0(tree.getTreechildren(), cmpr);
			}
			if (isPagingMold) tree.getPaginal().setActivePage(activePg);
				// Because of maintaining the number of the visible item, we cause
				// the wrong active page when dynamically add/remove the item (i.e. sorting).
				// Therefore, we have to reset the correct active page.
		} finally {
			Scopes.afterInterpret();
		}
		
		_ignoreSort = true;
		//maintain
		for (Iterator it = tree.getTreecols().getChildren().iterator();
		it.hasNext();) {
			final Treecol col = (Treecol)it.next();
			col.setSortDirection(
				col != this ? "natural": ascending ? "ascending": "descending");
		}
		_ignoreSort = false;

		// sometimes the items at client side are out of date
		tree.invalidate();
		
		return true;
	}
	/** Sorts the treechildren.
	 * @since 5.0.6
	 */
	private static void sort0(Treechildren treechildren, Comparator cmpr) {
		if (treechildren == null) return;
		Components.sort(treechildren.getChildren(), cmpr);
		for (Iterator it = treechildren.getChildren().iterator(); it.hasNext();) {
			Treeitem item = (Treeitem) it.next();
			sort0(item.getTreechildren(), cmpr);
		}
	}
	
	/** Sorts the treechildren ({@link Treeitem}) based on {@link #getSortAscending}
	 * and {@link #getSortDescending}.
	 *
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @param force whether to enforce the sorting no matter what the sort
	 * direction ({@link #getSortDirection}) is.
	 * If false, this method is the same as {@link #sort(boolean)}.
	 * @return whether the treechildren are sorted.
	 * @since 5.0.6
	 */
	public boolean sort(boolean ascending, boolean force) {
		if (force) setSortDirection("natural");
		return sort(ascending);
	}
	
	//-- event listener --//
	/** It invokes {@link #sort(boolean)} to sort list items and maintain
	 * {@link #getSortDirection}.
	 * @since 5.0.6
	 */
	public void onSort() {
		final String dir = getSortDirection();
		if ("ascending".equals(dir)) sort(false);
		else if ("descending".equals(dir)) sort(true);
		else if (!sort(true)) sort(false);
	}
	
	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-treecol" : _zclass;
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		if (!"none".equals(_sortDscNm))
			render(renderer, "sortDescending", _sortDscNm);

		if (!"none".equals(_sortAscNm))
			render(renderer, "sortAscending", _sortAscNm);
		
		if (!"natural".equals(_sortDir))
			render(renderer, "sortDirection", _sortDir);
		
		if (_maxlength > 0)
			renderer.render("maxlength", _maxlength);
		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Treecols))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
	
	//Cloneable//
	public Object clone() {
		final Treecol clone = (Treecol)super.clone();
		clone.fixClone();
		return clone;
	}
	private void fixClone() {
		if (_sortAsc instanceof TreeitemComparator) {
			final TreeitemComparator c = (TreeitemComparator)_sortAsc;
			if (c.getTreecol() == this && c.isAscending())
				_sortAsc =
					new TreeitemComparator(this, true, c.shallIgnoreCase());
		}
		if (_sortDsc instanceof TreeitemComparator) {
			final TreeitemComparator c = (TreeitemComparator)_sortDsc;
			if (c.getTreecol() == this && !c.isAscending())
				_sortDsc =
					new TreeitemComparator(this, false, c.shallIgnoreCase());
		}
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		boolean written = false;
		if (_sortAsc instanceof TreeitemComparator) {
			final TreeitemComparator c = (TreeitemComparator)_sortAsc;
			if (c.getTreecol() == this && c.isAscending()) {
				s.writeBoolean(true);
				s.writeBoolean(c.shallIgnoreCase());
				s.writeBoolean(c.byValue());
				written = true;
			}
		}
		if (!written) {
			s.writeBoolean(false);
			s.writeObject(_sortAsc);
		}

		written = false;
		if (_sortDsc instanceof TreeitemComparator) {
			final TreeitemComparator c = (TreeitemComparator)_sortDsc;
			if (c.getTreecol() == this && !c.isAscending()) {
				s.writeBoolean(true);
				s.writeBoolean(c.shallIgnoreCase());
				s.writeBoolean(c.byValue());
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
			_sortAsc = new TreeitemComparator(this, true, igcs, byval);
		} else {
			//bug #2830325 FieldComparator not castable to ListItemComparator
			_sortAsc = (Comparator)s.readObject();
		}

		b = s.readBoolean();
		if (b) {
			final boolean igcs = s.readBoolean();
			final boolean byval = s.readBoolean();
			_sortDsc = new TreeitemComparator(this, false, igcs, byval);
		} else {
			//bug #2830325 FieldComparator not castable to ListItemComparator
			_sortDsc = (Comparator)s.readObject();
		}
	}
}
