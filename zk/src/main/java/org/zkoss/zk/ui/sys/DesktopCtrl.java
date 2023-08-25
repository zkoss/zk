/* DesktopCtrl.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 29 08:47:19     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.zkoss.util.media.Media;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.impl.DesktopEventQueue;
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

	/** Returns the next available UUID for a page.
	 * The returned UUID is unique in the desktop.
	 * You can consider it as unique in the whole session, though
	 * it may not be true if {@link org.zkoss.zk.ui.ext.RawId} is used
	 * (developer's responsibility to avoid conflict),
	 * integer overflow (too many UUID in one session, which
	 * can be considered as impossible), or a custom ID generator
	 * ({@link org.zkoss.zk.ui.sys.IdGenerator}) is used.
	 * @since 5.0.3
	 */
	public String getNextUuid(Page page);

	/** Returns the next available UUID for a component.
	 * The returned UUID is unique in the desktop.
	 * You can consider it as unique in the whole session, though
	 * it may not be true if {@link org.zkoss.zk.ui.ext.RawId} is used
	 * (developer's responsibility to avoid conflict),
	 * integer overflow (too many UUID in one session, which
	 * can be considered as impossible), or a custom ID generator
	 * ({@link org.zkoss.zk.ui.sys.IdGenerator}) is used.
	 * @since 5.0.3
	 */
	public String getNextUuid(Component comp);

	/** Adds a component to this page.
	 * <p>It is used internally and developers shall not invoke it
	 * explicityly.
	 * @exception InternalError if there is a component associated with
	 * the same UUID
	 */
	public void addComponent(Component comp);

	/** Maps a component associated with the given UUID to this page.
	 * Notice that the given uuid can be different from comp's UUID
	 * ({@link Component#getUuid}).
	 * <p>If the given component is null, the mapping of UUID is removed.
	 * <p>Unlike {@link #addComponent} and {@link #removeComponent}, this method simply replaces
	 * the mapping if the given UUID is already mapped to the other component.
	 * <p>It is used internally and developers shall not invoke it
	 * explicitly.
	 * @param comp the component to associate with the given UUID.
	 * If not, the association (i.e., mapping) is removed.
	 * @return the previous component that was associated with the given UUID.
	 * @since 6.0.0
	 */
	public Component mapComponent(String uuid, Component comp);

	/** Removes a component to this page.
	 * <p>It is used internally and developers shall not invoke it
	 * explicitly.
	 * @return false always since 10.0.0 (Deprecated: <s>whether UUID is recycled. If true, the caller shall
	 * reset UUID of the give component.)
	 * @since 5.0.4
	 */
	public boolean removeComponent(Component comp);

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

	/** Called when the recovering failed.
	 */
	public void recoverDidFail(Throwable ex);

	/** Notification that the session, which owns this desktop,
	 * is about to be passivated (a.k.a., serialized) by the Web container.
	 */
	public void sessionWillPassivate(Session sess);

	/** Notification that the session, which owns this desktop,
	 * has just been activated (a.k.a., deserialized) by the Web container.
	 */
	public void sessionDidActivate(Session sess);

	/** Called when the desktop is about to be destroyed.
	 */
	public void destroy();

	/** Called when the desktop has been recycled.
	 * More precisely, it is called when the desktop is no longer used
	 * and ready to be re-used later.
	 * @since 5.0.7
	 */
	public void recycle();

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
	//Use the following pattern IF it is not in the SAME desktop's listener
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
	public Collection<EventProcessingThread> getSuspendedThreads();

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
	 * @param reserved reserved for future use.
	 * @return the media or null if not found.
	 */
	public Media getDownloadMedia(String medId, boolean reserved);

	/** Called when a component added or removed a listener for
	 * {@link Events#ON_PIGGYBACK}.
	 *
	 * <p>The implementation usually uses it to optimize whether to
	 * call the listener when {@link #onPiggyback} is called.
	 *
	 * @param comp the component that adds an listener for
	 * {@link Events#ON_PIGGYBACK}.
	 * The component may or may not be a root component.
	 * @param listen whether the listener is added (or removed).
	 * @since 3.0.0
	 */
	public void onPiggybackListened(Component comp, boolean listen);

	/** Called each time when ZK Update Engine retrieves events.
	 * It is used to implement the piggyback feature by posting
	 * the events (see {@link Events#ON_PIGGYBACK}).
	 * The implementation could post events here. It should not process
	 * event here (since event thread might be used).
	 *
	 * <p>Used only internally. Application developers shall not call it.
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
	 * If you want to use the default serverpush, use {@link Desktop#enableServerPush}
	 * instead. This method allows the caller to provide a server push
	 * for more control.
	 * <p>Example:
	 * <pre><code>desktop.enableServerPush(new PollingServerPush(1000,6000,5));</code></pre>
	 *
	 * <p>Notice: a server push controller can be used in one desktop.
	 * It cannot be shared.
	 *
	 * @param serverpush the server-push controller. If null,
	 * the server-push feature is disabled (for this desktop).
	 * Note: this method will invoke {@link ServerPush#start}, so the
	 * caller doesn't need to do it.
	 * @since 3.0.0
	 * @see Desktop#enableServerPush
	 */
	public boolean enableServerPush(ServerPush serverpush);

	/**
	 * Enable/Disable serverpush using reference counting, so that multiple enablers can 
	 * use the same serverpush and deregister whenever they want.
	 * @param enable true/false enable/disable serverpush
	 * @param enabler the same reference must be used to disable again
	 * @return Currently only used by {@link DesktopEventQueue} to enable several
	 * eventqueues to use the same {@link ServerPush} 
	 * @since 6.5.4
	 */
	public boolean enableServerPush(boolean enable, Serializable enabler);

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
	public Event beforeProcessEvent(Event event) throws Exception;

	/** Invokes {@link EventInterceptor#afterProcessEvent}
	 * registered by {@link Desktop#addListener}.
	 *
	 * <p>Note: it invokes
	 * {@link org.zkoss.zk.ui.util.Configuration#afterProcessEvent}
	 * automatically.
	 * @since 3.0.0
	 */
	public void afterProcessEvent(Event event) throws Exception;

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
	public void invokeExecutionInits(Execution exec, Execution parent) throws UiException;

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
	 * occurred before this method is called, or null if no exception at all.
	 * Note: you can manipulate the list directly to add or clean up exceptions.
	 * For example, if exceptions are fixed correctly, you can call errs.clear()
	 * such that no error message will be displayed at the client.
	 * @since 3.0.6
	 */
	public void invokeExecutionCleanups(Execution exec, Execution parent, List<Throwable> errs);

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

	//Response Utilities//
	/** Called when ZK Update Engine has sent a response to the client.
	 *
	 * @param reqId the request ID that the response is generated for.
	 * Ignore if null.
	 * @param resInfo the response. Ignored if reqId is null.
	 * @since 5.0.0
	 */
	public void responseSent(String reqId, Object resInfo);

	/** Returns the response for the last request, or null
	 * if no response yet, or the specified request ID doesn't match
	 * the last one (passed to {@link #responseSent}).
	 * <p>The return value is the value passed to resInfo when calling
	 * {@link #responseSent}.
	 * @since 5.0.0
	 */
	public Object getLastResponse(String reqId);

	/** Returns the sequence ID of the response.
	 * The client and server uses the sequence ID to make sure
	 * the responses are processed in the correct order.
	 *
	 * <p>The range of the sequence IDs is 1~999.
	 *
	 * @param advance whether to advance the number before returning.
	 * If true, the ID is increased and then returned.
	 * If false, the previous value is returned
	 * @since 3.5.0
	 */
	public int getResponseId(boolean advance);

	/** Sets the sequence ID of the response.
	 *
	 * <p>It is rarely called other than in the recovering mode, i.e.,
	 * {@link ExecutionCtrl#isRecovering} is true.
	 *
	 * @param resId a value between 1 and 999.
	 * You can reset the ID by passing a non-positive value.
	 * @since 3.5.0
	 */
	public void setResponseId(int resId);

	/** Adds the responses to the so-called piggy-back queue.
	 * The responses in the piggy-back queue will be sent in
	 * the next AU request.
	 * <p>This method is useful for working thread that
	 * wants to sent the responses back to the client.
	 * A typical example is the Comet-based server push.
	 *
	 * @param response the responses to be appended to the piggy-back queue.
	 * @param reset whether to reset the piggy-back queue after
	 * returning the queued responses.
	 * @return all responses in the piggy-back queue, or null
	 * if nothing in the queue.
	 * @since 5.0.0
	 */
	public List<AuResponse> piggyResponse(Collection<AuResponse> response, boolean reset);

	/** Schedules a task to run under the server push of the given desktop asynchronously.
	 * It is called by {@link org.zkoss.zk.ui.Executions#schedule}.
	 * Don't call it directly.
	 * <p>Like {@link #activateServerPush} and {@link #deactivateServerPush}, this method could
	 * be called in any thread, so the implementation of this method has to be safe for concurrent access.
	 * @param task the task to execute
	 * @param event the event to be passed to the task (i.e., the event listener).
	 * It could null or any instance as long as the task recognizes it.
	 * @exception IllegalStateException if the server push is not enabled.
	 * @exception DesktopUnavailableException if the desktop is removed
	 * (when activating).
	 * @since 5.0.6
	 */
	public <T extends Event> void scheduleServerPush(EventListener<T> task, T event);

	/** Returns if there is any scheduled task for server push.
	 * @since 5.0.6
	 */
	public boolean scheduledServerPush();

	/** Activates the current thread for accessing this desktop by the server push.
	 * It is called by {@link org.zkoss.zk.ui.Executions#activate}.
	 * Don't call it directly.
	 *
	 * <p>Like {@link #scheduleServerPush}, this method could
	 * be called in any thread, so it has to be safe for concurrent access.
	 * <p>Note: the server push must be enabled first (by use of
	 * {@link Desktop#enableServerPush}).
	 *
	 * @param timeout the maximum time to wait in milliseconds.
	 * Ignored (i.e., never timeout) if non-positive.
	 * @exception IllegalStateException if the server push is not enabled.
	 * @since 3.5.2
	 */
	public boolean activateServerPush(long timeout) throws InterruptedException;

	/** Deactivates the thread that has invoked {@link #activateServerPush}
	 * successfully.
	 * It is called by {@link org.zkoss.zk.ui.Executions#deactivate}.
	 * Don't call it directly.
	 * @since 3.5.2
	 */
	public void deactivateServerPush();

	/** Processes an AU request.
	 * Notice that not only the requests for a desktop but also the requests
	 * for any component in the desktop will go thru this method.
	 *
	 * <p>To override the default processing, register an AU request service
	 * {@link org.zkoss.zk.au.AuService} by invoking {@link Desktop#addListener}.
	 *
	 * <ol>
	 * <li>This method first invokes the registered AU request service
	 * ({@link org.zkoss.zk.au.AuService}) one-by-one, until
	 * the first one returns true.</li>
	 * <li>If none of them returns true or no AU service at all,
	 * it checks if the request is targeting a component
	 * (i.e., {@link AuRequest#getComponent} is not null).</li>
	 * <li>If it is targeting a component, it invokes
	 * {@link ComponentCtrl#service} to handle the service.</li>
	 * <li>If it is not targeting a component (i.e., targeting to
	 * this desktop), it handles as follows.
	 * <ul>
	 * <li>It handles the recognized requests, including
	 * onBookmarkChange, onURIChange and onClientInfo.</li>
	 * <li>If the request is not one of above, it converts the request to
	 * an event (by {@link Event#getEvent}) and then posts the event
	 * (by {@link Events#postEvent}).</li></ul></li>
	 * </ol>
	 *
	 * <p>Notice that the registered AU request service
	 * ({@link org.zkoss.zk.au.AuService}) will be called, no matter
	 * the request is targeting a component or a desktop.
	 * And, it can 'intercept' or 'filter' it by returning false.
	 *
	 * <p>To send responses to the client, use
	 * {@link org.zkoss.zk.ui.AbstractComponent#smartUpdate},
	 * {@link org.zkoss.zk.ui.AbstractComponent#response}
	 * or {@link Component#invalidate()}.
	 *
	 * <p>If you want to intercept events, you can register a listener implementing
	 * {@link EventInterceptor}, or overriding {@link #afterProcessEvent}.
	 *
	 * @param everError if any error ever occurred before
	 * processing this request. In other words, indicates if the previous
	 * request causes any exception.
	 * @since 5.0.0
	 */
	public void service(AuRequest request, boolean everError);

	/** Sets the execution (used to represent a lock).
	 * <p>Used only to implement {@link UiEngine}.
	 */
	public void setExecution(Execution exec);

	/** Returns the visualizer associated with this desktop.
	 * <p>Used only to implement {@link UiEngine}.
	 * @since 3.6.2
	 */
	public Visualizer getVisualizer();

	/** Sets the visualizer associated with is desktop.
	 * <p>Used only to implement {@link UiEngine}.
	 * @since 3.6.2
	 */
	public void setVisualizer(Visualizer uv);

	/** Returns the lock used to activate an execution.
	 * Before calling {@link #setVisualizer}, this object returned
	 * by this method must be locked first.
	 * <p>Used only to implement {@link UiEngine}.
	 * @since 3.6.2
	 */
	public Object getActivationLock();
}
