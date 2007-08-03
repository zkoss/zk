/* ServerPush.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug  3 16:39:18     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.Desktop;

/**
 * Represents a server-push controller.
 * A server-push controller is a plugin to provide the server-push feature
 * for ZK.
 * A server-push thread is a working thread that want to manipulate a desktop
 * whenever it wants, rather than in an event listener.
 *
 * <p>There are several to implement (or, to simulate) the server-push feature
 * on a HTTP-based application.
 *
 * <p>First, the server-push feature can be simulated by client's polling.
 * That is, the client polls the server for executing any pending
 * server-push threads.
 * The client can adjust the frequency based on the response time
 * (in proportion to the server load).
 * In this implementation, the event must be called onSPPolling,
 * and {@link #onPolling} will be called when this event is received.
 *
 * <p>Second, the server-push feature can be implemented by maintaining
 * a persistent connection between client and server. It is also
 * called Comet (see also <a href="http://en.wikipedia.org/wiki/Comet_%28programming%29">Comet</a>).
 *
 * <p>Third, the server-push feature can be simulated in a passive way.
 * That is, the pending pushes are executed only when the server
 * receives an event
 *
 * @author tomyeh
 * @since 2.5.0
 */
public interface ServerPush {
	/** Initializes the server-push controller.
	 * One server-push controller is associated with exactly one desktop.
	 *
	 * @return the client script codes (usually JavaScript) to initialize
	 * the server path.
	 */
	public String init(Desktop desktop);
	/** Called when the onSPPolling event is received.
	 * The client-polling-based implemenation usually checks whether
	 * any pending server-push threads. If true, execute them.
	 *
	 * <p>The implementation can return a command
	 * ({@link org.zkoss.zk.au.AuResponse} or its derived, say,
	 * {@link org.zkoss.zk.au.AuScript}) to adjust the polling
	 * frequency based on the loading of the server.
	 *
	 * <p>Like {@link #onPiggyback}, this method is called in
	 * the context of an event listener. In other words, the execution
	 * is activated and you can retrieve it by
	 * {@link org.zkoss.zk.ui.Executions#getCurrent}.
	 */
	public void onPolling();
	/** Called when {@link org.zkoss.zk.ui.event.Events#ON_PIGGYBACK}
	 * is received. The invocation is <i>passive</i> (i.e., triggered by
	 * other events, rather than spontaneous).
	 *
	 * <p>Like {@link #onPolling}, this method is called in
	 * the context of an event listener. In other words, the execution
	 * is activated and you can retrieve it by
	 * {@link org.zkoss.zk.ui.Executions#getCurrent}.
	 */
	public void onPiggyback();

	/** Called when a server-push thread is going to execute.
	 * The invoker of this method must invoke {@link #onDeactivate}
	 * in this finally clause.
	 *
	 * <p>Unlike {@link #onPolling} and {@link #onPiggyback},
	 * this method is NOT called in the context of an event listener.
	 * Rather, it is called in the thread of a server-push thread.
	 */
	public void onActivate();
	/** Called when the execution of a server-push thread is going to stop.
	 * @see #onActivate
	 */
	public void onDeactivate();
}
