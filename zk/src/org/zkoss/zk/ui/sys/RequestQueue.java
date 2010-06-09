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
	 * @since 3.0.5
	 */
	public boolean isEmpty();
	/** Returns the next request, or null if no more request.
	 * Onced returned, the request is removed from the queue.
	 */
	public AuRequest nextRequest();

	/** Adds a list of requests to the queue.
	 * Notice that when the request is added, it will be activated first,
	 * i.e., {@link AuRequest#activate} will be called before adding
	 * to queue. If the component is not found or does not belong to
	 * the current execution, it will be ignored.
	 */
	public void addRequests(Collection requests);

	/** Adds a request ID that uniquely identifies a request for
	 * performance measuarement {@link org.zkoss.zk.ui.util.PerformanceMeter}.
	 *
	 * @param requestId the request ID (never null)
	 * @since 3.0.5
	 */
	public void addPerfRequestId(String requestId);
	/** Clears all request IDs that were added by {@link #addPerfRequestId}
	 * for performance measurement.
	 * It is usually called after all requests are processed
	 * ({@link #isEmpty} is true).
	 *
	 * @return a list of request IDs that were added by {@link #addPerfRequestId},
	 * or null if no request ID was added.
	 * @since 3.0.5
	 */
	public Collection clearPerfRequestIds();
}
