/* Paginal.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 17 15:27:03     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.ext;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.EventListener;

/**
 * Represents a component that is used to control how to display
 * other components in multiple pages.
 * In other words, it represents a paging controller.
 *
 * <p>It is used to provide an abstract contract between controller and controllee.
 * For example, {@link org.zkoss.zul.Paging} is a paging controller
 * while {@link org.zkoss.zul.Grid} is a paging controllee.
 * In other words, {@link org.zkoss.zul.Grid} (contains long content)
 * can be controlled by {@link org.zkoss.zul.Paging}.
 *
 * <p>The paging controller must implement this interface, while
 * the paging controllee shall access only this interface (rather than
 * {@link org.zkoss.zul.Paging} or so).
 *
 * <p>Note: If a component supports multiple pages but it cannot
 * be controlled by a paging controller, it shall implement
 * {@link Pageable} instead.
 * Example, {@link org.zkoss.zul.Treechildren}
 * is {@link Pageable} but not controllable by {@link Paginal}.
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
 * @author tomyeh
 * @see Paginated
 */
public interface Paginal extends Pageable {
	/** Returns the total number of items.
	 */
	public int getTotalSize();
	/** Sets the total number of items.
	 */
	public void setTotalSize(int size) throws WrongValueException;

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
