/* Pageable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 29 17:17:01     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.ext;

import org.zkoss.zk.ui.WrongValueException;

/**
 * Represents a component that can be displayed in multiple pages
 * but cannot be controlled by an external paging controller.
 *
 * <ul>
 * <li>{@link Paginated}: a multi-page component whose pagination
 * is controlled by an external page controlle ({@link Paginal}).</li>
 * <li>{@link Paginal}: the paging controller used to control
 * the pagination of {@link Paginated}.</li>
 * <li>{@link Pageable}: a multi-page component that handles pagination
 * by itself.</li>
 * </ul>
 *
 * <p>Note: a component that can be displayed in multiple pages
 * can be implemented in two ways.
 *
 * <p>1) If it can be controlled by an external paging controller
 * (i.e., {@link Paginal}), it shall hold an reference to one of
 * the paging controllers. And, it doesn't need to implement {@link Pageable}.
 * Example: grid, listbox and tree.
 *
 * <p>2) If it cannot be controlled by external controller, it shall
 * implement the {@link Pageable} interface.
 *
 * @author tomyeh
 * @since 2.4.1
 * @see Paginal
 */
public interface Pageable {
	/** Returns the number of items per page.
	 * <p>Default: 20.
	 */
	public int getPageSize();
	/** Sets the number of items per page.
	 */
	public void setPageSize(int size) throws WrongValueException;
	/** Returns the number of pages.
	 * Note: there is at least one page even no item at all.
	 */
	public int getPageCount();
	/** Returns the active page (starting from 0).
	 */
	public int getActivePage();
	/** Sets the active page (starting from 0).
	 */
	public void setActivePage(int pg) throws WrongValueException;
}
