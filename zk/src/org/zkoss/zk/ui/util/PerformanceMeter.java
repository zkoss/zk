/* PerformanceMeter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 24 16:03:07     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Execution;

/**
 * A listener to measure the performance of the processing of client
 * requests.
 *
 * <p>Note: ZK doesn't fork another low-priority thread to call the
 * methods defined in this listener. It is the implementation's job to
 * minimize the overhead when calculating the performance data.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface PerformanceMeter {
	/** Called to notify when the client starts to send the request
	 * to server.
	 *
	 * @param requestId a desktop-wide unique ID to identify a request.
	 * Note: it is not the same as {@link org.zkoss.zk.ui.sys.ExecutionCtrl#getRequestId}
	 * @param exec the execution. You can retrieve the desktop,
	 * session and user's info from it. But, don't access the component
	 * in this method since it is not safe (exec is not activated).
	 * @param time the time representing the number of milliseconds
	 * between midnight January 1, 1970 (UTC) to when the client starts
	 * the request. It is client's time, not server's.
	 */
	public void requestStartAtClient(
		String requestId, Execution exec, long time);
	/** Called to notify when the client has received the response
	 * (but not processed yet).
	 *
	 * <p>Note: the invocation of this method doesn't take place
	 * immediately. Rather it is piggyback when the client is
	 * sending another request. It also means that this method might
	 * not be called for each request (due to no further request).
	 *
	 * <p>Note to version prior to 3.0.8:<br/>
	 * This method is introduced since 3.0.8.
	 * Prior to 3.0.8, {@link #requestCompleteAtClient} is actually
	 * the time when the client recieved the response rather than
	 * when the response has been processed.
	 *
	 * @param requestId a desktop-wide unique ID to identify a request.
	 * @param exec the execution. You can retrieve the desktop,
	 * session and user's info from it. But, don't access the component
	 * in this method since it is not safe (exec is not activated).
	 * @param time the time representing the number of milliseconds
	 * between midnight January 1, 1970 (UTC) to when the client has
	 * completed the process. It is client's time, not server's.
	 * @since 3.0.8
	 */
	public void requestReceiveAtClient(
		String requestId, Execution exec, long time);
	/** Called to notify when the client has completed the processing of
	 * the response.
	 *
	 * <p>Note: the invocation of this method doesn't take place
	 * immediately. Rather it is piggyback when the client is
	 * sending another request. It also means that this method might
	 * not be called for each request (due to no further request).
	 *
	 * @param requestId a desktop-wide unique ID to identify a request.
	 * @param exec the execution. You can retrieve the desktop,
	 * session and user's info from it. But, don't access the component
	 * in this method since it is not safe (exec is not activated).
	 * @param time the time representing the number of milliseconds
	 * between midnight January 1, 1970 (UTC) to when the client has
	 * completed the process. It is client's time, not server's.
	 */
	public void requestCompleteAtClient(
		String requestId, Execution exec, long time);

	/** Called to notify when the server receives the request.
	 *
	 * @param requestId a desktop-wide unique ID to identify a request.
	 * It is the same request ID as {@link #requestStartAtClient}.
	 * @param exec the execution. You can retrieve the desktop,
	 * session and user's info from it. But, don't access the component
	 * in this method since it is not safe (exec is not activated).
	 * @param time the time representing the number of milliseconds
	 * between midnight January 1, 1970 (UTC) to when the server receives
	 * the request. It is server's time.
	 */
	public void requestStartAtServer(
		String requestId, Execution exec, long time);
	/** Called to notify when the server has completed the processing of
	 * the request.
	 *
	 * @param requestId a desktop-wide unique ID to identify a request.
	 * @param exec the execution. You can retrieve the desktop,
	 * session and user's info from it. But, don't access the component
	 * in this method since it is not safe (exec is not activated).
	 * @param time the time representing the number of milliseconds
	 * between midnight January 1, 1970 (UTC) to when the server has
	 * completed the process. It is server's time.
	 */
	public void requestCompleteAtServer(
		String requestId, Execution exec, long time);
}
