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
package org.zkoss.zul.ext;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.EventListener;

/**
 * Represents a component that provides the paging for long content.
 * It is used to provide an abstract contract between controller and controllee.
 * For example, {@link org.zkoss.zul.Paging} is a paging controller
 * while {@link org.zkoss.zul.Grid} is a paging controllee.
 * In other words, {@link org.zkoss.zul.Grid} (contains long content)
 * can be controlled by {@link org.zkoss.zul.Paging}.
 *
 * <p>The paging controller must implement this interface, while
 * the paging controllee shall access only this interface (rather than
 * {@link org.zkoss.zul.Paging} or so).
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
	public void setPageSize(int size) throws WrongValueException;
	/** Returns the total number of items.
	 */
	public int getTotalSize();
	/** Sets the total number of items.
	 */
	public void setTotalSize(int size) throws WrongValueException;
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

	/** Returns the number of page anchors shall appear at the client. 
	 *
	 * <p>Default: 10.
	 */
	public int getPageIncrement();
	/** Sets the number of page anchors shall appear at the client.
	 */
	public void setPageIncrement(int pginc) throws WrongValueException;

	/** Returns whether to show the detailed info, such as {@link #getTotalSize}.
	 */
	public boolean isDetailed();
	/** Sets whether to show the detailed info, such as {@link #getTotalSize}.
	 */
	public void setDetailed(boolean detailed);

	/** Adds an event listener to specified event for this component.
	 * The second registration is ignored and false is returned.
	 *
	 * <p>The onPaging event is sent with an instance of
	 * {@link org.zkoss.zul.event.PagingEvent} to notify which page
	 * is selected.
	 *
	 * @param evtnm what event to listen (never null)
	 * @return whether the listener is added; false if it was added before
	 * @see org.zkoss.zul.event.PagingEvent
	 */
	public boolean addEventListener(String evtnm, EventListener listener);
	/** Removes an event listener.
	 * @return whether the listener is removed; false if it was never added.
	 */
	public boolean removeEventListener(String evtnm, EventListener listener);
}
