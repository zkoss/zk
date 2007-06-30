/* Pageable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 29 17:17:01     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.ext;

import org.zkoss.zk.ui.WrongValueException;

/**
 * The interface that a component must implement, if it supports
 * the onPaging event.
 *
 * @author tomyeh
 * @since 2.4.1
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
