/* Listheader.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.Comparator;
import org.zkoss.zk.ui.Components;//for javadoc
import org.zkoss.zk.ui.WrongValueException;

/**
 * The list header which defines the attributes and header of a columen of a
 * list box. Its parent must be {@link Listhead}.
 * 
 * <p>
 * Difference from XUL:
 * <ol>
 * <li>There is no listcol in ZUL because it is merged into {@link Listheader}.
 * Reason: easier to write Listbox.</li>
 * </ol>
 * <p>
 * Default {@link #getZclass}: z-listheader (since 5.0.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Listheader extends org.zkoss.zul.impl.api.HeaderElement {

	/**
	 * Returns the listbox that this belongs to.
	 */
	public org.zkoss.zul.api.Listbox getListboxApi();

	/**
	 * Returns the sort direction.
	 * <p>
	 * Default: "natural".
	 */
	public String getSortDirection();

	/**
	 * Sets the sort direction. This does not sort the data, it only serves as
	 * an indicator as to how the list is sorted.
	 * 
	 * <p>
	 * If you use {@link #sort(boolean)} to sort list items, the sort direction
	 * is maintained automatically. If you want to sort it in customized way,
	 * you have to set the sort direction manaully.
	 * 
	 * @param sortDir
	 *            one of "ascending", "descending" and "natural"
	 */
	public void setSortDirection(String sortDir) throws WrongValueException;

	/**
	 * Sets the type of the sorter. You might specify either "auto" or "none".
	 * 
	 * <p>
	 * If "auto" is specified, it will call {@link #setSortAscending} and/or
	 * {@link #setSortDescending} are called with
	 * {@link org.zkoss.zul.ListitemComparator}, if {@link #getSortDescending}
	 * and/or {@link #getSortAscending} are null. If you assigned a comparator
	 * to them, it won't be affected. The auto created comparator is
	 * case-insensitive.
	 * 
	 * <p>
	 * If "none" is specified, both {@link #setSortAscending} and
	 * {@link #setSortDescending} are called with null. Therefore, no more
	 * sorting is available to users for this column.
	 */
	public void setSort(String type) throws ClassNotFoundException,
	InstantiationException, IllegalAccessException;

	/**
	 * Returns the ascending sorter, or null if not available.
	 */
	public Comparator getSortAscending();

	/**
	 * Sets the ascending sorter, or null for no sorter for the ascending order.
	 * 
	 * @param sorter
	 *            the comparator used to sort the ascending order. If you are
	 *            using the group feature, you can pass an instance of
	 *            {@link org.zkoss.zul.GroupComparator} to have a better
	 *            control. If an instance of
	 *            {@link org.zkoss.zul.GroupComparator} is passed,
	 *            {@link org.zkoss.zul.GroupComparator#compareGroup} is used to
	 *            group elements, and
	 *            {@link org.zkoss.zul.GroupComparator#compare} is used to sort
	 *            elements with a group. Otherwise, {@link Comparator#compare}
	 *            is used to group elements and sort elements within a group.
	 */
	public void setSortAscending(Comparator sorter);

	/**
	 * Sets the ascending sorter with the class name, or null for no sorter for
	 * the ascending order.
	 */
	public void setSortAscending(String clsnm) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException;

	/**
	 * Returns the descending sorter, or null if not available.
	 */
	public Comparator getSortDescending();

	/**
	 * Sets the descending sorter, or null for no sorter for the descending
	 * order.
	 * 
	 * @param sorter
	 *            the comparator used to sort the ascending order. If you are
	 *            using the group feature, you can pass an instance of
	 *            {@link org.zkoss.zul.GroupComparator} to have a better
	 *            control. If an instance of
	 *            {@link org.zkoss.zul.GroupComparator} is passed,
	 *            {@link org.zkoss.zul.GroupComparator#compareGroup} is used to
	 *            group elements, and
	 *            {@link org.zkoss.zul.GroupComparator#compare} is used to sort
	 *            elements with a group. Otherwise, {@link Comparator#compare}
	 *            is used to group elements and sort elements within a group.
	 */
	public void setSortDescending(Comparator sorter);

	/**
	 * Sets the descending sorter with the class name, or null for no sorter for
	 * the descending order.
	 */
	public void setSortDescending(String clsnm) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException;

	/**
	 * Returns the maximal length of each item's label. Default: 0 (no limit).
	 */
	public int getMaxlength();

	/**
	 * Sets the maximal length of each item's label.
	 */
	public void setMaxlength(int maxlength);

	/**
	 * Returns the column index, starting from 0.
	 */
	public int getColumnIndex();

	/**
	 * Sorts the list items based on {@link #getSortAscending} and
	 * {@link #getSortDescending}, if {@link #getSortDirection} doesn't matches
	 * the ascending argument.
	 * 
	 * <p>
	 * It checks {@link #getSortDirection} to see whether sorting is required,
	 * and update {@link #setSortDirection} after sorted. For example, if
	 * {@link #getSortDirection} returns "ascending" and the ascending argument
	 * is false, nothing happens. To enforce the sorting, you can invoke
	 * {@link #setSortDirection} with "natural" before invoking this method.
	 * Alternatively, you can invoke {@link #sort(boolean, boolean)} instead.
	 * 
	 * <p>
	 * It sorts the listitem by use of {@link Components#sort} data (i.e.,
	 * {@link Grid#getModel} is null).
	 * 
	 * <p>
	 * On the other hand, it invokes {@link org.zkoss.zul.ListModelExt#sort} to
	 * sort the list item, if live data (i.e., {@link Listbox#getModel} is not
	 * null). In other words, if you use the live data, you have to implement
	 * {@link org.zkoss.zul.ListModelExt} to sort the live data explicitly.
	 * 
	 * @param ascending
	 *            whether to use {@link #getSortAscending}. If the corresponding
	 *            comparator is not set, it returns false and does nothing.
	 * @return whether the list items are sorted.
	 * @exception UiException
	 *                if {@link Listbox#getModel} is not null but
	 *                {@link org.zkoss.zul.ListModelExt} is not implemented.
	 */
	public boolean sort(boolean ascending);

	/**
	 * Sorts the list items based on {@link #getSortAscending} and
	 * {@link #getSortDescending}.
	 * 
	 * @param ascending
	 *            whether to use {@link #getSortAscending}. If the corresponding
	 *            comparator is not set, it returns false and does nothing.
	 * @param force
	 *            whether to enforce the sorting no matter what the sort
	 *            direction ({@link #getSortDirection}) is. If false, this
	 *            method is the same as {@link #sort(boolean)}.
	 * @return whether the rows are sorted.
	 */
	public boolean sort(boolean ascending, boolean force);

}
