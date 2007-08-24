/* PerformanceMeter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 24 16:03:07     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Desktop;

/**
 * A listener to measure the performance of certain activities.
 *
 * <p>Note: ZK doesn't fork another low-priority thread to call the
 * methods defined in this listener. It is the implementation's job to
 * minimize the overhead when calculating the performance data.
 *
 * @author tomyeh
 * @since 2.5.0
 */
public interface PerformanceMeter {
	/** Called to notify when the client starts to send the request
	 * to server.
	 *
	 * @param requestId a system-wide unique ID to identify a request.
	 * @param desktop the desktop that the request is associated with.
	 * The session and user's info can be retrieved from it.
	 * @param time the time representing the number of milliseconds
	 * between midnight January 1, 1970 (UTC) to when the client starts
	 * the request. It is client's time, not server's.
	 */
	public void requestStartAtClient(
		String requestId, Desktop desktop, long time);
	/** Called to notify when the client has completed the processing of
	 * the response.
	 *
	 * <p>Note: the invocation of this method doesn't take place
	 * immediately. Rather it is piggyback when the client is
	 * sending another request. It also means that this method might
	 * not be called for each request (due to no further request).
	 *
	 * @param requestId a system-wide unique ID to identify a request.
	 * @param desktop the desktop that the request is associated with.
	 * The session and user's info can be retrieved from it.
	 * @param time the time representing the number of milliseconds
	 * between midnight January 1, 1970 (UTC) to when the client has
	 * completed the process. It is client's time, not server's.
	 */
	public void requestCompleteAtClient(
		String requestId, Desktop desktop, long time);

	/** Called to notify when the server receives the request.
	 *
	 * @param requestId a system-wide unique ID to identify a request.
	 * It is the same request ID as {@link #requestStartAtClient}.
	 * @param desktop the desktop that the request is associated with.
	 * The session and user's info can be retrieved from it.
	 * @param time the time representing the number of milliseconds
	 * between midnight January 1, 1970 (UTC) to when the server receives
	 * the request. It is server's time.
	 */
	public void requestStartAtServer(
		String requestId, Desktop desktop, long time);
	/** Called to notify when the server has completed the processing of
	 * the request.
	 *
	 * @param requestId a system-wide unique ID to identify a request.
	 * @param desktop the desktop that the request is associated with.
	 * The session and user's info can be retrieved from it.
	 * @param time the time representing the number of milliseconds
	 * between midnight January 1, 1970 (UTC) to when the server has
	 * completed the process. It is server's time.
	 */
	public void requestCompleteAtServer(
		String requestId, Desktop desktop, long time);
}
