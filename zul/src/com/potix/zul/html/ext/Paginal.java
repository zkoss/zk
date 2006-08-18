/* Paginal.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 17 15:27:03     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zul.html.ext;

import com.potix.zk.ui.event.EventListener;

/**
 * Represents a component that provides the paging for long content.
 * It is used to provide an abstract contract between controller and controllee.
 * For example, {@link com.potix.zul.html.Paging} is a paging controller
 * while {@link com.potix.zul.html.Grid} is a paging controllee.
 * In other words, {@link com.potix.zul.html.Grid} (contains long content)
 * can be controlled by {@link com.potix.zul.html.Paging}.
 *
 * <p>The paging controller must implement this interface, while
 * the paging controllee shall access only this interface (rather than
 * {@link com.potix.zul.html.Paging} or so).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Paginal {
	/** Returns the number of items per page.
	 * <p>Default: 20.
	 */
	public int getPageSize();
	/** Sets the number of items per page.
	 */
	public void setPageSize(int size);
	/** Returns the total number of items.
	 */
	public int getTotalSize();
	/** Sets the total number of items.
	 */
	public void setTotalSize(int size);
	/** Returns the number of pages.
	 * Note: there is at least one page even no item at all.
	 */
	public int getPageCount();

	/** Returns the active page (starting from 0).
	 */
	public int getActivePage();
	/** Sets the active page (starting from 0).
	 */
	public void setActivePage(int pg);

	/** Adds an event listener to specified event for this component.
	 * The second registration is ignored and false is returned.
	 *
	 * <p>The onPaging event is sent with an instance of
	 * {@link com.potix.zul.html.event.PagingEvent} to notify which page
	 * is selected.
	 *
	 * @param evtnm what event to listen (never null)
	 * @return whether the listener is added; false if it was added before
	 * @see com.potix.zul.html.event.PagingEvent
	 */
	public boolean addEventListener(String evtnm, EventListener listener);
	/** Removes an event listener.
	 * @return whether the listener is removed; false if it was never added.
	 */
	public boolean removeEventListener(String evtnm, EventListener listener);
}
