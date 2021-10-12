/* PageableModel.java

	Purpose:

	Description:

	History:
			Fri Mar 16 17:15:11 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.PagingListener;
import org.zkoss.zul.ext.Pageable;

/**
 * Provide methods to manage PagingListeners
 * @author charlesqiu
 * @see PagingEvent
 * @since 8.5.2
 */
public interface PageableModel extends Pageable {
	/**
	 * A flag different from standard PagingEvent, differentiating standard
	 * PagingEvent from Paging component and PagingEvent from PageableModel
	 */
	String INTERNAL_EVENT = "internalModelEvent";

	/**
	 * Adds a listener to the list of listeners to be notified when a PagingEvent
	 * happens outside of standard Paging component
	 *
	 * @param listener
	 */
	void addPagingEventListener(PagingListener listener);

	/**
	 * Removes a listener from the list of listeners to be notified when a PagingEvent
	 * happens outside of standard Paging component
	 *
	 * @param listener
	 */
	void removePagingEventListener(PagingListener listener);
}
