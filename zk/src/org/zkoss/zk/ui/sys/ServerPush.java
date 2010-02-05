/* ServerPush.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug  3 16:39:18     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;

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
 * (in proportion to the server load) (see {@link org.zkoss.zk.ui.impl.PollingServerPush} for details).
 * To poll, the client usually send the dummy command that does nothing
 * but trigger {@link #onPiggyback} to be execute.
 *
 * <p>Second, the server-push feature can be implemented by maintaining
 * a persistent connection between client and server. It is also
 * called Comet (see also <a href="http://en.wikipedia.org/wiki/Comet_%28programming%29">Comet</a>).
 *
 * <p>Third, the server-push feature can be simulated in a passive way.
 * That is, it doesn't poll at all. Rather {@link #onPiggyback} is called
 * automatically when the user trigger some other events.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface ServerPush {
	/** Returns if the working thread of the server push is active.
	 * In other words, it is true if it is between
	 * {@link org.zkoss.zk.ui.Executions#activate} and
	 * {@link org.zkoss.zk.ui.Executions#deactivate}.
	 * @since 3.5.2
	 */
	public boolean isActive();

	/** Stats and initializes the server-push controller.
	 * One server-push controller is associated with exactly one desktop.
	 *
	 * <p>{@link #start} is called when {@link Desktop#enableServerPush}
	 * is called to enable the server-push feature for the specified
	 * desktop.
	 */
	public void start(Desktop desktop);
	/** Stops and cleans up the server-push controller.
	 *
	 * <p>{@link #stop} is called when {@link Desktop#enableServerPush}
	 * is called to disable the server-push feature, or when the desktop
	 * is being removed.
	 */
	public void stop();

	/** @deprecated As of release 5.0.0, use the preferences instead.
	 * Refer to {@link org.zkoss.zk.ui.impl.PollingServerPush}
	 */
	public void setDelay(int min, int max, int factor);

	/** Activate the current thread (which must be a server-push thread).
	 * It causes the current thread to wait until the desktop is available
	 * to access, the desktop no longer exists,
	 * some other thread interrupts this thread,
	 * or a certain amount of real time has elapsed.
	 *
	 * <p>The invoker of this method must invoke {@link #deactivate}
	 * in this finally clause.
	 *
	 * <p>Note: the activation is applied to the desktop that was
	 * assigned by {@link #start}.
	 *
	 * <p>Unlike {@link #onPiggyback},
	 * this method is NOT called in the context of an event listener.
	 * Rather, it is called in the thread of a server-push thread.
	 *
	 * @param timeout the maximum time to wait in milliseconds.
	 * Ingored (i.e., never timeout) if non-positive.
	 * @exception InterruptedException if it is interrupted by other thread
	 * @exception DesktopUnavailableException if the desktop is removed
	 * (when activating).
	 * @return whether it is activated or it is timeout.
	 * The only reason it returns false is timeout.
	 */
	public boolean activate(long timeout)
	throws InterruptedException, DesktopUnavailableException;
	/** Deactvates the current thread (which must be a server-push thread).
	 *
	 * @param stop whether to stop after deactivate
	 * @return true if it is stopped
	 * @see #activate
	 * @since 3.5.2
	 */
	public boolean deactivate(boolean stop);

	/** Called when {@link org.zkoss.zk.ui.event.Events#ON_PIGGYBACK}
	 * is received. The invocation is <i>passive</i> (i.e., triggered by
	 * other events, rather than spontaneous).
	 *
	 * <p>This method is called in
	 * the context of an event listener. In other words, the execution
	 * is activated and you can retrieve it by
	 * {@link org.zkoss.zk.ui.Executions#getCurrent}.
	 */
	public void onPiggyback();
}
