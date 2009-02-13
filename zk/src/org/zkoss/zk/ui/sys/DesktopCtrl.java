/* DesktopCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 29 08:47:19     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.Collection;
import java.util.List;

import org.zkoss.util.media.Media;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.EventInterceptor;

/**
 * An addition interface to {@link Desktop}
 * for implementation.
 *
 * <p>Note: applications shall never access this interface.
 *
 * @author tomyeh
 */
public interface DesktopCtrl {
	/** Returns the request queue.
	 */
	public RequestQueue getRequestQueue();

	/** Returns the next available key which is unique in the whole desktop.
	 */
	public int getNextKey();
	/** Returns the next available UUID for a component.
	 * The returned UUID is unique in the desktop.
	 * You can consider it as unique in the whole session, though
	 * it may not be true if {@link org.zkoss.zk.ui.ext.RawId} is used
	 * (developer's responsibility to avoid conflict),
	 * or integer overflow (too many UUID in one session, which
	 * can be considered as impossible).
	 */
	public String getNextUuid();

	/** Adds a component to this page.
	 * <p>It is used internally and developers shall not invoke it
	 * explicityly.
	 */
	public void addComponent(Component comp);
	/** Removes a component to this page.
	 * <p>It is used internally and developers shall not invoke it
	 * explicityly.
	 */
	public void removeComponent(Component comp);

	/** Adds a page to this desktop.
	 * It must be called when a page is created.
	 *
	 * <p>This is one of the only few method you could access
	 * before activating an execution.
	 */
	public void addPage(Page page);
	/** Removes a page from this desktop.
	 * <p>NOTE: once a page is removed, you can NOT add it back.
	 * You shall just GC it.
	 */
	public void removePage(Page page);

	/** Sets the execution (used to represent a lock).
	 * Called only internally (by UIEngine's implementation to activate
	 * an execution).
	 */
	public void setExecution(Execution exec);

	/** Sets the bookmark when receiving the onBookmarkChange command
	 * from the client.
	 */
	public void setBookmarkByClient(String name);

	/** Sets the desktop identifier.
	 *
	 * <p>It is callable only if it is the recovering phase, i.e.,
	 * {@link ExecutionCtrl#isRecovering} is true.
	 * In other words, callable only in the invocation of
	 * {@link FailoverManager#recover}.
	 *
	 * @exception IllegalStateException if it is NOT in recovering.
	 */
	public void setId(String id);
	/** Called when the recoving failed.
	 */
	public void recoverDidFail(Throwable ex);

	/** Notification that the session, which owns this desktop,
	 * is about to be passivated (aka., serialized) by the Web container.
	 */
	public void sessionWillPassivate(Session sess);
	/** Notification that the session, which owns this desktop,
	 * has just been activated (aka., deserialized) by the Web container.
	 */
	public void sessionDidActivate(Session sess);

	/** Called when the desktop is about to be destroyed.
	 */
	public void destroy();

	/** Returns a collection of suspended event processing threads, or empty
	 * if no suspended thread at all.
	 *
	 * <p>An event processing thread is an instance of
	 * {@link EventProcessingThread}
	 *
	 * <p>Note: if you access this method NOT in an event listener for
	 * the SAME desktop, you have to synchronize the iteration
	 * (though the returned collection is synchronized).
	 * Of course, it is always safe to test whether it is empty
	 * ({@link Collection#isEmpty}).
	 *
	 * <pre><code>
//Use the following pathern IF it is not in the SAME desktop's listener
Collection c = otherDesktop.getSuspendedThreads();
if (c.isEmpty()) {
	//do something accordingly
} else {
  synchronized (c) {
    for (Iterator it = c.iterator(); it.hasNext();) {
      //...
    }
  }
}</code></pre>
	 */
	public Collection getSuspendedThreads();
	/** Ceases the specified event thread.
	 *
	 * @param cause an arbitrary text to describe the cause.
	 * It will be the message of the thrown InterruptedException.
	 * @return true if the event processing thread is ceased successfully;
	 * false if no such thread or it is not suspended.
	 */
	public boolean ceaseSuspendedThread(EventProcessingThread evtthd, String cause);

	/** Returns the media that is associated with
	 * {@link Desktop#getDownloadMediaURI}, or
	 * null if not found.
	 *
	 * <p>This method is used internally. Developers rarely need to
	 * access this method.
	 *
	 * @param remove whether to remove it from cache once returned.
	 * @return the media or null if not found.
	 */
	public Media getDownloadMedia(String medId, boolean remove);

	/** Called when a component added or removed a listener for
	 * {@link Events#ON_PIGGYBACK}.
	 *
	 * <p>The implementation usualy uses it to optimize whether to
	 * call the listener when {@link #onPiggyback} is called.
	 *
	 * @param comp the component that adds an listener for
	 * {@link Events#ON_PIGGYBACK}.
	 * The component may or may not be a root component.
	 * @param listen whether the listener is added (or removed).
	 * @since 3.0.0
	 */
	public void onPiggybackListened(Component comp, boolean listen);
	/** Called each time ZK Update Engine processes all events.
	 * It is used to implement the piggyback feature
	 * (see {@link Events#ON_PIGGYBACK}).
	 *
	 * <p>Used only internally. Application develepers shall not call it.
	 *
	 * @since 3.0.0
	 */
	public void onPiggyback();
	/** Returns the server-push controller, or null if it is not enabled
	 * yet.
	 */
	public ServerPush getServerPush();
	/** Enables the server-push feature with the specified server-push
	 * controller.
	 *
	 * @param serverpush the server-push controller. If null,
	 * the server-push feature is disabled (for this desktop).
	 * Note: this method will invoke {@link ServerPush#start}, so the
	 * caller doesn't need to do it.
	 * @since 3.0.0
	 * @see Desktop#enableServerPush
	 */
	public boolean enableServerPush(ServerPush serverpush);

	/** Invokes {@link EventInterceptor#beforeSendEvent}
	 * registered by {@link Desktop#addListener}.
	 *
	 * <p>Note: it invokes
	 * {@link org.zkoss.zk.ui.util.Configuration#beforeSendEvent}
	 * automatically.
	 * @since 3.0.0
	 */
	public Event beforeSendEvent(Event event);
	/** Invokes {@link EventInterceptor#beforePostEvent}
	 * registered by {@link Desktop#addListener}.
	 *
	 * <p>Note: it invokes
	 * {@link org.zkoss.zk.ui.util.Configuration#beforePostEvent}
	 * automatically.
	 * @since 3.0.0
	 */
	public Event beforePostEvent(Event event);
	/** Invokes {@link EventInterceptor#beforeProcessEvent}
	 * registered by {@link Desktop#addListener}.
	 *
	 * <p>Note: it invokes
	 * {@link org.zkoss.zk.ui.util.Configuration#beforeProcessEvent}
	 * automatically.
	 * @since 3.0.0
	 */
	public Event beforeProcessEvent(Event event);
	/** Invokes {@link EventInterceptor#afterProcessEvent}
	 * registered by {@link Desktop#addListener}.
	 *
	 * <p>Note: it invokes
	 * {@link org.zkoss.zk.ui.util.Configuration#afterProcessEvent}
	 * automatically.
	 * @since 3.0.0
	 */
	public void afterProcessEvent(Event event);
	/** Invokes {@link org.zkoss.zk.ui.util.DesktopCleanup#cleanup} for each relevant
	 * listener registered by {@link Desktop#addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>It never throws an exception.
	 *
	 * @since 3.0.6
	 */
	public void invokeDesktopCleanups();

	/** Invokes {@link org.zkoss.zk.ui.util.ExecutionInit#init} for each relevant
	 * listener registered by {@link Desktop#addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * @param exec the execution that is created
	 * @param parent the previous execution, or null if no previous at all
	 * @exception UiException to prevent an execution from being created
	 * @since 3.0.6
	 */
	public void invokeExecutionInits(Execution exec, Execution parent)
	throws UiException;
	/** Invokes {@link org.zkoss.zk.ui.util.ExecutionCleanup#cleanup} for each relevant
	 * listener registered by {@link Desktop#addListener}.
	 *
 	 * <p>Used only internally.
	 *
	 * <p>It never throws an exception but logs and adds it to the errs argument,
	 * if not null.
	 *
	 * @param exec the execution that is being destroyed
	 * @param parent the previous execution, or null if no previous at all
	 * @param errs a list of exceptions (java.lang.Throwable) if any exception
	 * occured before this method is called, or null if no exeption at all.
	 * Note: you can manipulate the list directly to add or clean up exceptions.
	 * For example, if exceptions are fixed correctly, you can call errs.clear()
	 * such that no error message will be displayed at the client.
	 * @since 3.0.6
	 */
	public void invokeExecutionCleanups(Execution exec, Execution parent, List errs);

	/** Invokes {@link org.zkoss.zk.ui.util.UiLifeCycle#afterComponentAttached}.
	 * @since 3.0.6
	 */
	public void afterComponentAttached(Component comp, Page page);
	/** Invokes {@link org.zkoss.zk.ui.util.UiLifeCycle#afterComponentDetached}.
	 * @since 3.0.6
	 */
	public void afterComponentDetached(Component comp, Page prevpage);
	/** Invokes {@link org.zkoss.zk.ui.util.UiLifeCycle#afterComponentMoved}.
	 *
	 * @param prevparent the previous parent. If it is the same as
	 * comp's {@link Component#getParent}, comp is moved in the same parent.
	 * @since 3.0.6
	 */
	public void afterComponentMoved(Component parent, Component child, Component prevparent);

	/** Called when ZK Update Engine has sent a collection of responses
	 * to the client.
	 *
	 * @param reqId the sequence ID of the request that the collection
	 * of responses are generated for. It must not be null.
	 * @param responses the collection of responses. It must not be null.
	 * @since 3.0.5
	 */
	public void responseSent(String reqId, Collection responses);
	/** Returns the collection of responses for the last request, or null
	 * if no response yet, or the specified request ID doesn't match
	 * the last one (passed to {@link #responseSent}).
	 *
	 * @since 3.0.5
	 */
	public Collection getLastResponse(String reqId);

	/** @deprecated As of release 3.0.5, replaced with {@link #responseSent}.
	 */
	public int getResponseSequence(boolean advance);
	/** @deprecated As of release 3.0.5, replaced with {@link #responseSent}.
	 */
	public void setResponseSequence(int seqId);
}
