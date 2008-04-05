/* RequestQueue.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 24 11:00:33     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.Collection;

import org.zkoss.zk.au.AuRequest;

/**
 * A queue of {@link AuRequest}.
 * There is one queue for each desktop.
 *
 * <p>Implementation Note:
 * Unlike only of desktop members, this class must be thread-safe.
 *
 * @author tomyeh
 */
public interface RequestQueue {
	/** Returns if no more request available in the queue.
	 *
	 * @return whether no more request is available in the queue.
	 * @since 2.4.3
	 */
	public boolean isEmpty();
	/** Returns the next request, or null if no more request.
	 * Onced returned, the request is removed from the queue.
	 */
	public AuRequest nextRequest();

	/** Adds a list of requests to the queue.
	 */
	public void addRequests(Collection requests);
}
