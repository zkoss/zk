/* Listheader.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug  5 13:06:59     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.Messages;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zk.ui.ext.Scopes;
import org.zkoss.zul.ext.GroupsSortableModel;
import org.zkoss.zul.ext.Sortable;
import org.zkoss.zul.impl.HeaderElement;
import org.zkoss.zul.mesg.MZul;

/**
 * The list header which defines the attributes and header of a column
 * of a list box.
 * Its parent must be {@link Listhead}.
 *
 * <p>Difference from XUL:
 * <ol>
 * <li>There is no listcol in ZUL because it is merged into {@link Listheader}.
 * Reason: easier to write Listbox.</li>
 * </ol>
 * <p>Default {@link #getZclass}: z-listheader.(since 5.0.0)
 * @author tomyeh
 */
public class Listheader extends HeaderElement {
	private static final long serialVersionUID = 20080218L;

	private String _sortDir = "natural";
	private transient Comparator _sortAsc, _sortDsc;
	private String _sortAscNm = "none";
	private String _sortDscNm = "none";
	private Object _value;
	private int _maxlength;
	private boolean _ignoreSort = false;
	private boolean _isCustomAscComparator = false;
	private boolean _isCustomDscComparator = false;

	static {
		addClientEvent(Listheader.class, Events.ON_SORT, CE_DUPLICATE_IGNORE);
		addClientEvent(Listheader.class, Events.ON_GROUP, CE_DUPLICATE_IGNORE);
		addClientEvent(Listheader.class, Events.ON_UNGROUP, CE_DUPLICATE_IGNORE);
	}

	public Listheader() {
	}

	public Listheader(String label) {
		super(label);
	}

	/* Constructs a list header with label and image.
	 *
	 * @param src the URI of the image. Ignored if null or empty.
	 */
	public Listheader(String label, String src) {
		super(label, src);
	}

	/* Constructs a list header with label, image and width.
	 *
	 * @param src the URI of the image. Ignored if null or empty.
	 * @param width the width of the column. Ignored if null or empty.
	 * @since 3.0.4
	 */
	public Listheader(String label, String src, String width) {
		super(label, src);
		setWidth(width);
	}

	/** Returns the listbox that this belongs to.
	 */
	public Listbox getListbox() {
		final Component comp = getParent();
		return comp != null ? (Listbox) comp.getParent() : null;
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 3.6.0
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) _value;
	}

	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 3.6.0
	 */
	public <T> void setValue(T value) {
		_value = value;
	}

	/** Returns the sort direction.
	 * <p>Default: "natural".
	 */
	public String getSortDirection() {
		return _sortDir;
	}

	/** Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the list is sorted. (unless the listbox has "autosort" attribute)
	 *
	 * <p>If you use {@link #sort(boolean)} to sort list items,
	 * the sort direction is maintained automatically.
	 * If you want to sort it in customized way, you have to set the
	 * sort direction manually.
	 *
	 * @param sortDir one of "ascending", "descending" and "natural"
	 */
	public void setSortDirection(String sortDir) throws WrongValueException {
		if (sortDir == null
				|| (!"ascending".equals(sortDir) && !"descending".equals(sortDir) && !"natural".equals(sortDir)))
			throw new WrongValueException("Unknown sort direction: " + sortDir);
		if (!Objects.equals(_sortDir, sortDir)) {
			_sortDir = sortDir;
			if (!"natural".equals(sortDir) && !_ignoreSort) {
				Listbox listbox = getListbox();
				if (listbox != null && listbox.isAutosort()) {
					doSort("ascending".equals(sortDir));
				}
			}
			smartUpdate("sortDirection", _sortDir); //don't use null because sel.js assumes it
		}
	}

	/** Sets the type of the sorter.
	 * You might specify either "auto", "auto(FIELD_NAME1[,FIELD_NAME2] ...)"(since 3.5.3),
	 * "auto(<i>number</i>)" (since 5.0.6) or "none".
	 * 
	 * <p>If "client" or "client(number)" is specified,
	 * the sort functionality will be done by Javascript at client without notifying
	 * to server, that is, the order of the component in the row is out of sync.
	 * <ul>
	 * <li> "client" : it is treated by a string</li>
	 * <li> "client(number)" : it is treated by a number</li>
	 * </ul>
	 * <p>Note: client sorting cannot work in model case. (since 5.0.0)
	 * 
	 * <p>If "auto" is specified,
	 * {@link #setSortAscending} and/or {@link #setSortDescending} 
	 * are called with {@link ListitemComparator}, if
	 * {@link #getSortDescending} and/or {@link #getSortAscending} are null.
	 * If you assigned a comparator to them, it won't be affected.
	 * The auto created comparator is case-insensitive.
	 *
	 * <p>If "auto(FIELD_NAME1, FIELD_NAME2, ...)" is specified,
	 * {@link #setSortAscending} and/or {@link #setSortDescending} 
	 * are called with {@link FieldComparator}, if
	 * {@link #getSortDescending} and/or {@link #getSortAscending} are null.
	 * If you assigned a comparator to them, it won't be affected.
	 * The auto created comparator is case-sensitive.
	 * 
	 * <p>If "auto(LOWER(FIELD_NAME))" or "auto(UPPER(FIELD_NAME))" is specified,
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
	 */
	public void setSort(String type) {
		if (type == null)
			return;
		if (type.startsWith("client")) {
			try {
				setSortAscending(type);
				setSortDescending(type);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex); //not possible to throw ClassNotFoundException...
			}
		} else if ("auto".equals(type)) {
			if (getSortAscending() == null)
				setSortAscending(new ListitemComparator(this, true, true));
			if (getSortDescending() == null)
				setSortDescending(new ListitemComparator(this, false, true));
		} else if (!Strings.isBlank(type) && type.startsWith("auto")) {
			final int j = type.indexOf('(');
			final int k = type.lastIndexOf(')');
			if (j >= 0 && k >= 0) {
				final String name = type.substring(j + 1, k);
				char cc;
				int index = -1;
				if (name.length() > 0 && (cc = name.charAt(0)) >= '0' && cc <= '9')
					if ((index = Integer.parseInt(name)) < 0)
						throw new IllegalArgumentException("Nonnegative number is required: " + name);
				if (getSortAscending() == null || !_isCustomAscComparator) {
					if (index < 0)
						setSortAscending(new FieldComparator(name, true));
					else
						setSortAscending(new ArrayComparator(index, true));
					_isCustomAscComparator = false;
				}
				if (getSortDescending() == null || !_isCustomDscComparator) {
					if (index < 0)
						setSortDescending(new FieldComparator(name, false));
					else
						setSortDescending(new ArrayComparator(index, false));
					_isCustomDscComparator = false;
				}
			} else {
				throw new UiException("Unknown sort type: " + type);
			}
		} else if ("none".equals(type)) {
			setSortAscending((Comparator) null);
			setSortDescending((Comparator) null);
		}
	}

	/** Returns the ascending sorter, or null if not available.
	 */
	public Comparator getSortAscending() {
		return _sortAsc;
	}

	/** Sets the ascending sorter, or null for no sorter for
	 * the ascending order.
	 *
	 * @param sorter the comparator used to sort the ascending order.
	 * If you are using the group feature, you can pass an instance of
	 * {@link GroupComparator} to have a better control.
	 * If an instance of {@link GroupComparator} is passed,
	 * {@link GroupComparator#compareGroup} is used to group elements,
	 * and {@link GroupComparator#compare} is used to sort elements
	 * with a group.
	 * Otherwise, {@link Comparator#compare} is used to group elements
	 * and sort elements within a group.
	 */
	public void setSortAscending(Comparator sorter) {
		if (!Objects.equals(_sortAsc, sorter)) {
			_sortAsc = sorter;
			_isCustomAscComparator = _sortAsc != null;
			String nm = _isCustomAscComparator ? "fromServer" : "none";
			if (!_sortAscNm.equals(nm)) {
				_sortAscNm = nm;
				smartUpdate("sortAscending", _sortAscNm);
			}
		}
	}

	/** Sets the ascending sorter with the class name, or null for
	 * no sorter for the ascending order.
	 */
	public void setSortAscending(String clsnm)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (!Strings.isBlank(clsnm) && clsnm.startsWith("client") && !_sortAscNm.equals(clsnm)) {
			_sortAscNm = clsnm;
			smartUpdate("sortAscending", clsnm);
		} else
			setSortAscending(toComparator(clsnm));
	}

	/** Returns the descending sorter, or null if not available.
	 */
	public Comparator getSortDescending() {
		return _sortDsc;
	}

	/** Sets the descending sorter, or null for no sorter for the
	 * descending order.
	 *
	 * @param sorter the comparator used to sort the ascending order.
	 * If you are using the group feature, you can pass an instance of
	 * {@link GroupComparator} to have a better control.
	 * If an instance of {@link GroupComparator} is passed,
	 * {@link GroupComparator#compareGroup} is used to group elements,
	 * and {@link GroupComparator#compare} is used to sort elements
	 * with a group.
	 * Otherwise, {@link Comparator#compare} is used to group elements
	 * and sort elements within a group.
	 */
	public void setSortDescending(Comparator sorter) {
		if (!Objects.equals(_sortDsc, sorter)) {
			_sortDsc = sorter;
			_isCustomDscComparator = _sortDsc != null;
			String nm = _isCustomDscComparator ? "fromServer" : "none";
			if (!_sortDscNm.equals(nm)) {
				_sortDscNm = nm;
				smartUpdate("sortDescending", _sortDscNm);
			}
		}
	}

	/** Sets the descending sorter with the class name, or null for
	 * no sorter for the descending order.
	 */
	public void setSortDescending(String clsnm)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (!Strings.isBlank(clsnm) && clsnm.startsWith("client") && !_sortDscNm.equals(clsnm)) {
			_sortDscNm = clsnm;
			smartUpdate("sortDescending", clsnm);
		} else
			setSortDescending(toComparator(clsnm));
	}

	private Comparator toComparator(String clsnm)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (clsnm == null || clsnm.length() == 0)
			return null;

		final Page page = getPage();
		final Class cls = page != null ? page.resolveClass(clsnm) : Classes.forNameByThread(clsnm);
		if (cls == null)
			throw new ClassNotFoundException(clsnm);
		if (!Comparator.class.isAssignableFrom(cls))
			throw new UiException("Comparator must be implemented: " + clsnm);
		return (Comparator) cls.newInstance();
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
		if (maxlength < 0)
			maxlength = 0;
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			smartUpdate("maxlength", maxlength);
		}
	}

	/** Returns the column index, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator(); it.hasNext(); ++j)
			if (it.next() == this)
				break;
		return j;
	}

	/** Sorts the list items based on {@link #getSortAscending}
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
	 * <p>It sorts the listitem by use of {@link Components#sort}
	 * data (i.e., {@link Grid#getModel} is null).
	 *
	 * <p>On the other hand, it invokes {@link Sortable#sort} to sort
	 * the list item, if live data (i.e., {@link Listbox#getModel} is not null).
	 * In other words, if you use the live data, you have to implement
	 * {@link Sortable} to sort the live data explicitly.
	 *
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @return whether the list items are sorted.
	 * @exception UiException if {@link Listbox#getModel} is not
	 * null but {@link Sortable} is not implemented.
	 */
	public boolean sort(boolean ascending) {
		final String dir = getSortDirection();
		if (ascending) {
			if ("ascending".equals(dir))
				return false;
		} else {
			if ("descending".equals(dir))
				return false;
		}
		return doSort(ascending);
	}

	/** Sorts the list items based on {@link #getSortAscending}
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
		if (force)
			setSortDirection("natural");
		return sort(ascending);
	}

	/**/ boolean doSort(boolean ascending) {
		final Comparator cmpr = ascending ? _sortAsc : _sortDsc;
		if (cmpr == null)
			return false;

		final Listbox box = getListbox();
		if (box == null)
			return false;

		//comparator might be zscript
		Scopes.beforeInterpret(this);
		try {
			final ListModel model = box.getModel();
			boolean isPagingMold = box.inPagingMold();
			int activePg = isPagingMold ? box.getPaginal().getActivePage() : 0;
			if (model != null) { //live data
				if (model instanceof GroupsSortableModel) {
					sortGroupsModel(box, (GroupsSortableModel) model, cmpr, ascending);
				} else {
					if (!(model instanceof Sortable))
						throw new UiException(GroupsSortableModel.class + " or " + Sortable.class
								+ " must be implemented in " + model.getClass().getName());
					sortListModel((Sortable) model, cmpr, ascending);
				}
			} else { //not live data
				sort0(box, cmpr);
			}
			if (isPagingMold)
				box.getPaginal().setActivePage(activePg);
			// Because of maintaining the number of the visible item, we cause
			// the wrong active page when dynamically add/remove the item (i.e. sorting).
			// Therefore, we have to reset the correct active page.
		} finally {
			Scopes.afterInterpret();
		}

		_ignoreSort = true;
		//maintain
		for (Iterator it = box.getListhead().getChildren().iterator(); it.hasNext();) {
			final Listheader hd = (Listheader) it.next();
			hd.setSortDirection(hd != this ? "natural" : ascending ? "ascending" : "descending");
		}
		_ignoreSort = false;

		// sometimes the items at client side are out of date
		box.invalidate();

		return true;
	}

	private void fixDirection(Listbox listbox, boolean ascending) {
		_ignoreSort = true;
		//maintain
		for (Iterator it = listbox.getListhead().getChildren().iterator(); it.hasNext();) {
			final Listheader hd = (Listheader) it.next();
			hd.setSortDirection(hd != this ? "natural" : ascending ? "ascending" : "descending");
		}
		_ignoreSort = false;
	}

	/**
	 * Groups and sorts the items ({@link Listitem}) based on
	 * {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * 
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @return whether the rows are grouped.
	 * @since 6.5.0
	 */
	public boolean group(boolean ascending) {
		final String dir = getSortDirection();
		if (ascending) {
			if ("ascending".equals(dir))
				return false;
		} else {
			if ("descending".equals(dir))
				return false;
		}
		final Comparator<?> cmpr = ascending ? _sortAsc : _sortDsc;
		if (cmpr == null)
			return false;

		final Listbox listbox = getListbox();
		if (listbox == null)
			return false;

		//comparator might be zscript
		Scopes.beforeInterpret(this);
		try {
			final ListModel model = listbox.getModel();
			int index = listbox.getListhead().getChildren().indexOf(this);
			if (model != null) { //live data
				if (!(model instanceof GroupsSortableModel))
					throw new UiException(
							GroupsSortableModel.class + " must be implemented in " + model.getClass().getName());
				groupGroupsModel((GroupsSortableModel) model, cmpr, ascending, index);
			} else { // not live data
				final List<Listitem> items = listbox.getItems();
				if (items.isEmpty())
					return false; //Avoid listbox with null group
				if (listbox.hasGroup()) {
					for (Listgroup group : new ArrayList<Listgroup>(listbox.getGroups()))
						group.detach(); // Groupfoot is removed automatically, if any.
				}

				Comparator<?> cmprx;
				if (cmpr instanceof GroupComparator) {
					cmprx = new GroupToComparator((GroupComparator) cmpr);
				} else {
					cmprx = cmpr;
				}

				final List<Listitem> children = new LinkedList<Listitem>(items);
				items.clear();
				sortCollection(children, cmprx);

				Listitem previous = null;
				for (Listitem item : children) {
					if (previous == null || compare(cmprx, previous, item) != 0) {
						//new group
						final List<Listcell> cells = item.getChildren();
						if (cells.size() < index)
							throw new IndexOutOfBoundsException("Index: " + index + " but size: " + cells.size());
						Listgroup group;
						Listcell cell = cells.get(index);
						if (cell.getLabel() != null) {
							group = new Listgroup(cell.getLabel());
						} else {
							Component cc = cell.getFirstChild();
							if (cc instanceof Label) {
								String val = ((Label) cc).getValue();
								group = new Listgroup(val);
							} else {
								group = new Listgroup(Messages.get(MZul.GRID_OTHER));
							}
						}
						listbox.appendChild(group);
					}
					listbox.appendChild(item);
					previous = item;
				}

				if (cmprx != cmpr)
					sort0(listbox, cmpr); //need to sort each group
			}
		} finally {
			Scopes.afterInterpret();
		}

		fixDirection(listbox, ascending);

		// sometimes the items at client side are out of date
		listbox.invalidate();

		return true;
	}

	@SuppressWarnings("unchecked")
	private void groupGroupsModel(GroupsSortableModel model, Comparator cmpr, boolean ascending, int index) {
		model.group(cmpr, ascending, index);
	}

	@SuppressWarnings("unchecked")
	private static void sortCollection(List<Listitem> comps, Comparator cmpr) {
		Collections.sort(comps, cmpr);
	}

	@SuppressWarnings("unchecked")
	private static int compare(Comparator cmpr, Object a, Object b) {
		return cmpr.compare(a, b);
	}

	@SuppressWarnings("unchecked")
	private void sortGroupsModel(Listbox box, GroupsSortableModel model, Comparator cmpr, boolean ascending) {
		model.sort(cmpr, ascending, box.getListhead().getChildren().indexOf(this));
	}

	@SuppressWarnings("unchecked")
	private void sortListModel(Sortable model, Comparator cmpr, boolean ascending) {
		model.sort(cmpr, ascending);
	}

	/** Sorts the items. If with group, each group is sorted independently.
	 */
	@SuppressWarnings("unchecked")
	private static void sort0(Listbox box, Comparator cmpr) {
		if (box.hasGroup())
			for (Listgroup g : box.getGroups()) {
				int index = g.getIndex() + 1;
				Components.sort(box.getItems(), index, index + g.getItemCount(), cmpr);
			}
		else
			Components.sort(box.getItems(), cmpr);
	}

	//-- event listener --//
	/**
	 * Invokes a sorting action based on a {@link SortEvent} and maintains
	 * {@link #getSortDirection}.
	 * @since 6.5.0
	 */
	public void onSort(SortEvent event) {
		sort(event.isAscending());
	}

	/**
	 * Internal use only.
	 * @since 6.5.0
	 */
	public void onGroupLater(SortEvent event) {
		group(event.isAscending());
	}

	/**
	 * Ungroups and sorts the items ({@link Listitem}) based on the ascending.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * 
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @since 6.5.0
	 */
	public void ungroup(boolean ascending) {
		final Comparator<?> cmpr = ascending ? _sortAsc : _sortDsc;
		if (cmpr != null) {

			final Listbox listbox = getListbox();
			if (listbox.getModel() == null) {

				// comparator might be zscript
				Scopes.beforeInterpret(this);
				try {
					final List<Listitem> items = listbox.getItems();
					if (listbox.hasGroup()) {
						for (Listgroup group : new ArrayList<Listgroup>(listbox.getGroups()))
							group.detach(); // Listgroupfoot is removed
											// automatically, if any.
					}

					Comparator<?> cmprx;
					if (cmpr instanceof GroupComparator) {
						cmprx = new GroupToComparator((GroupComparator) cmpr);
					} else {
						cmprx = cmpr;
					}

					final List<Listitem> children = new LinkedList<Listitem>(items);
					items.clear();
					sortCollection(children, cmprx);
					for (Component c : children)
						listbox.appendChild(c);
				} finally {
					Scopes.afterInterpret();
				}
			}
			fixDirection(listbox, ascending);

			// sometimes the items at client side are out of date
			listbox.invalidate();
		}
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-listheader" : _zclass;
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Listhead))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}

	/** Processes an AU request.
	 * <p>Default: in addition to what are handled by its superclass, it also 
	 * handles onSort.
	 * @since 6.5.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_SORT)) {
			SortEvent evt = SortEvent.getSortEvent(request);
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_GROUP)) {
			final Map<String, Object> data = request.getData();
			final boolean ascending = AuRequests.getBoolean(data, "");
			Events.postEvent(new SortEvent(cmd, this, ascending));

			// internal use, and it should be invoked after onGroup event.
			Events.postEvent(-1000, new SortEvent("onGroupLater", this, ascending));
		} else if (cmd.equals(Events.ON_UNGROUP)) {
			final Map<String, Object> data = request.getData();
			final boolean ascending = AuRequests.getBoolean(data, "");
			ungroup(ascending);
			Events.postEvent(new SortEvent(cmd, request.getComponent(), ascending));
		} else
			super.service(request, everError);
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
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

	//Cloneable//
	public Object clone() {
		final Listheader clone = (Listheader) super.clone();
		clone.fixClone();
		return clone;
	}

	private void fixClone() {
		if (_sortAsc instanceof ListitemComparator) {
			final ListitemComparator c = (ListitemComparator) _sortAsc;
			if (c.getListheader() == this && c.isAscending())
				_sortAsc = new ListitemComparator(this, true, c.shallIgnoreCase());
		}
		if (_sortDsc instanceof ListitemComparator) {
			final ListitemComparator c = (ListitemComparator) _sortDsc;
			if (c.getListheader() == this && !c.isAscending())
				_sortDsc = new ListitemComparator(this, false, c.shallIgnoreCase());
		}
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();

		boolean written = false;
		if (_sortAsc instanceof ListitemComparator) {
			final ListitemComparator c = (ListitemComparator) _sortAsc;
			if (c.getListheader() == this && c.isAscending()) {
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
		if (_sortDsc instanceof ListitemComparator) {
			final ListitemComparator c = (ListitemComparator) _sortDsc;
			if (c.getListheader() == this && !c.isAscending()) {
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

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		boolean b = s.readBoolean();
		if (b) {
			final boolean igcs = s.readBoolean();
			final boolean byval = s.readBoolean();
			_sortAsc = new ListitemComparator(this, true, igcs, byval);
		} else {
			//bug #2830325 FieldComparator not castable to ListItemComparator
			_sortAsc = (Comparator) s.readObject();
		}

		b = s.readBoolean();
		if (b) {
			final boolean igcs = s.readBoolean();
			final boolean byval = s.readBoolean();
			_sortDsc = new ListitemComparator(this, false, igcs, byval);
		} else {
			//bug #2830325 FieldComparator not castable to ListItemComparator
			_sortDsc = (Comparator) s.readObject();
		}
	}

	private static class GroupToComparator implements Comparator {
		private final GroupComparator _gcmpr;

		private GroupToComparator(GroupComparator gcmpr) {
			_gcmpr = gcmpr;
		}

		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			return _gcmpr.compareGroup(o1, o2);
		}
	}

	//B70-ZK-1816, also add in zk 8, ZK-2660
	protected void updateByClient(String name, Object value) {
		if ("visible".equals(name))
			this.setVisibleDirectly(((Boolean) value).booleanValue());
		else
			super.updateByClient(name, value);
	}
}
