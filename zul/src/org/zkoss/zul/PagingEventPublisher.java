/* PagingEventPublisher.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 23 18:23:15 2015, Created by Christopher

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.PagingListener;

/**
 * Provide methods to manage PagingListeners
 * @author Christopher
 * @see PagingEvent
 * @since 8.0.0
 */
public interface PagingEventPublisher {
	/**
	 * A flag different from standard PagingEvent, differentiating standard 
	 * PagingEvent from Paging component and PagingEvent from PagingEventPublisher
	 */
	public static final String INTERNAL_EVENT = "internalModelEvent";
	/**
	 * Adds a listener to the list of listeners to be notified when a PagingEvent
	 * happens outside of standard Paging component
	 * @param listener
	 */
	public void addPagingEventListener(PagingListener listener);
	/**
	 * Removes a listener from the list of listeners to be notified when a PagingEvent
	 * happens outside of standard Paging component
	 * @param listener
	 */
	public void removePagingEventListener(PagingListener listener);
}
