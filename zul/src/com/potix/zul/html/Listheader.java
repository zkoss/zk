/* Listheader.java

{{IS_NOTE
	$Id: Listheader.java,v 1.8 2006/05/26 04:05:55 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Aug  5 13:06:59     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Iterator;
import java.util.List;
import java.util.Comparator;

import com.potix.lang.Objects;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;

import com.potix.zul.html.impl.HeaderElement;

/**
 * The list header which defines the attributes and header of a columen
 * of a list box.
 * Its parent must be {@link Listhead}.
 *
 * <p>Difference from XUL:
 * <ol>
 * <li>There is no listcol in ZUL because it is merged into {@link Listheader}.
 * Reason: easier to write Listbox.</li>
 * </ol>
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.8 $ $Date: 2006/05/26 04:05:55 $
 */
public class Listheader extends HeaderElement {
	private String _sortDir = "natural";
	private Comparator _sortAsc, _sortDsc;
	private int _maxlength;

	public Listheader() {
	}
	public Listheader(String label) {
		setLabel(label);
	}
	public Listheader(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/** Returns the listbox that this belongs to.
	 */
	public Listbox getListbox() {
		final Component comp = getParent();
		return comp != null ? (Listbox)comp.getParent(): null;
	}
	/** Returns the listhead that this belongs to.
	 */
	public Listhead getListhead() {
		return (Listhead)getParent();
	}

	/** Returns the sort direction.
	 * <p>Default: "natural".
	 */
	public String getSortDirection() {
		return _sortDir;
	}
	/** Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the list is sorted. You would set this
	 * yourself after sorting the data.
	 * @param sortDir one of "ascending", "descending" and "natural"
	 */
	public void setSortDirection(String sortDir) throws WrongValueException {
		if (sortDir == null || (!"ascending".equals(sortDir)
		&& !"descending".equals(sortDir) && !"natural".equals(sortDir)))
			throw new WrongValueException("Unknown sort direction: "+sortDir);
		if (!Objects.equals(_sortDir, sortDir)) {
			_sortDir = sortDir;
		}
	}

	/** Returns the ascending sorter, or null if not available.
	 */
	public Comparator getSortAscending() {
		return _sortAsc;
	}
	/** Sets the ascending sorter, or null for no ascending order.
	 */
	public void setSortAscending(Comparator sorter) {
		if (!Objects.equals(_sortAsc, sorter)) {
			if (sorter == null) smartUpdate("zk_asc", null);
			else if (_sortAsc == null) smartUpdate("zk_asc", "true");
			_sortAsc = sorter;
		}
	}
	/** Sets the ascending sorter with the class name, or null for
	 * no ascending order.
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
	/** Sets the descending sorter, or null for no descending order.
	 */
	public void setSortDescending(Comparator sorter) {
		if (!Objects.equals(_sortDsc, sorter)) {
			if (sorter == null) smartUpdate("zk_dsc", null);
			else if (_sortDsc == null) smartUpdate("zk_dsc", "true");
			_sortDsc = sorter;
		}
	}
	/** Sets the descending sorter with the class name, or null for
	 * no descending order.
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

	/** Returns the maximal length of each item's label.
	 * <p>Note: DBCS counts  two bytes (range 0x4E00~0x9FF).
	 * Default: 0 (no limit).
	 */
	public int getMaxlength() {
		return _maxlength;
	}
	/** Sets the maximal length of each item's label.
	 */
	public void setMaxlength(int maxlength) {
		if (maxlength < 0) maxlength = 0;
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			invalidateCells(INNER);
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

	/** Invalidates the relevant cells. */
	private void invalidateCells(Range range) {
		final Listbox listbox = getListbox();
		if (listbox == null || listbox.isHtmlSelect())
			return;

		final int jcol = getColumnIndex();
		for (Iterator it = listbox.getItems().iterator(); it.hasNext();) {
			final Listitem li = (Listitem)it.next();
			final List chs = li.getChildren();
			if (jcol < chs.size())
				((Component)chs.get(jcol)).invalidate(range);
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(80);
		if (_sortAsc != null) sb.append(" zk_asc=\"true\"");
		if (_sortDsc != null) sb.append(" zk_dsc=\"true\"");

		final String attrs = super.getOuterAttrs();
		if (sb.length() == 0) return attrs;
		return sb.insert(0, attrs).toString();
	}

	/** Invalidates the whole box. */
	protected void invalidateWhole() {
		final Listbox box = getListbox();
		if (box != null) box.invalidate(INNER);
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Listhead))
			throw new UiException("Wrong parent: "+parent);
		super.setParent(parent);
	}
	public void invalidate(Range range) {
		super.invalidate(range);
		initAtClient();
	}
	private void initAtClient() {
		final Listbox box = getListbox();
		if (box != null) box.initAtClient();
	}
}
