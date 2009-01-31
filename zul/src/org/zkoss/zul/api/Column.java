/* Column.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.Comparator;
import org.zkoss.zk.ui.Components;//for javadoc
import org.zkoss.zk.ui.WrongValueException;

/**
 * A single column in a {@link Columns} element. Each child of the
 * {@link Column} element is placed in each successive cell of the grid. The
 * column with the most child elements determines the number of rows in each
 * column.
 * 
 * <p>
 * The use of column is mainly to define attributes for each cell in the grid.
 * 
 * <p>
 * Default {@link #getZclass}: z-column. (since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Column extends org.zkoss.zul.impl.api.HeaderElement {

	/** Returns the grid that contains this column. */
	public org.zkoss.zul.api.Grid getGridApi();

	/**
	 * Returns the sort direction.
	 * <p>
	 * Default: "natural".
	 */
	public String getSortDirection();

	/**
	 * Sets the sort direction. This does not sort the data, it only serves as
	 * an indicator as to how the grid is sorted.
	 * 
	 * <p>
	 * If you use {@link #sort(boolean)} to sort rows ({@link Row}), the sort
	 * direction is maintained automatically. If you want to sort it in
	 * customized way, you have to set the sort direction manaully.
	 * 
	 * @param sortDir
	 *            one of "ascending", "descending" and "natural"
	 */
	public void setSortDirection(String sortDir) throws WrongValueException;

	/** Sets the type of the sorter.
	 * You might specify either "auto", "auto(FIELD_NAME1[,FIELD_NAME2] ...)" or "none".
	 *
	 * <p>If "auto" is specified,
	 * {@link #setSortAscending} and/or {@link #setSortDescending} 
	 * are called with {@link org.zkoss.zul.RowComparator}, if
	 * {@link #getSortDescending} and/or {@link #getSortAscending} are null.
	 * If you assigned a comparator to them, it won't be affected.
	 * The auto created comparator is case-insensitive.
	 *
	 * <p>If "auto(FIELD_NAME1, FIELD_NAME2, ...)" is specified,
	 * {@link #setSortAscending} and/or {@link #setSortDescending} 
	 * are called with {@link org.zkoss.zul.FieldComparator}, if
	 * {@link #getSortDescending} and/or {@link #getSortAscending} are null.
	 * If you assigned a comparator to them, it won't be affected.
	 * The auto created comparator is case-insensitive.

	 * <p>If "none" is specified, both {@link #setSortAscending} and
	 * {@link #setSortDescending} are called with null.
	 * Therefore, no more sorting is available to users for this column.
	 * @since 3.5.3
	 */
	public void setSort(String type);

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
	 * Sorts the rows ({@link Row}) based on {@link #getSortAscending} and
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
	 * It sorts the rows by use of {@link Components#sort}, if not live data
	 * (i.e., {@link Grid#getModel} is null).
	 * 
	 * <p>
	 * On the other hand, it invokes {@link org.zkoss.zul.ListModelExt#sort} to
	 * sort the rows, if live data (i.e., {@link Grid#getModel} is not null). In
	 * other words, if you use the live data, you have to implement
	 * {@link org.zkoss.zul.ListModelExt} to sort the live data explicitly.
	 * 
	 * @param ascending
	 *            whether to use {@link #getSortAscending}. If the corresponding
	 *            comparator is not set, it returns false and does nothing.
	 * @return whether the rows are sorted.
	 * @exception UiException
	 *                if {@link Grid#getModel} is not null but
	 *                {@link org.zkoss.zul.ListModelExt} is not implemented.
	 */
	public boolean sort(boolean ascending);

	/**
	 * Sorts the rows ({@link Row}) based on {@link #getSortAscending} and
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

	/**
	 * Groups and sorts the rows ({@link Row}) based on
	 * {@link #getSortAscending}. If the corresponding comparator is not set, it
	 * returns false and does nothing.
	 * 
	 * @param ascending
	 *            whether to use {@link #getSortAscending}. If the corresponding
	 *            comparator is not set, it returns false and does nothing.
	 * @return whether the rows are grouped.
	 */
	public boolean group(boolean ascending);

}
