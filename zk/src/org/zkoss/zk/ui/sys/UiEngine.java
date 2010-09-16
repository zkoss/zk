/* UiEngine.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  9 12:58:20     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.io.IOException;
import java.io.Writer;

import org.zkoss.json.JSONArray;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriter;

/**
 * UI engine is reponsible to process requests from the client,
 * sends the response back to the client with the assistent of
 * {@link ExecutionCtrl}.
 *
 * <p>{@link ExecutionCtrl} encapsulates protocol-dependent codes,
 * such that UiEngine works independent of any protocol (such as HTTP).
 *
 * <p>Note: each application (a ServletContext in HTTP) has its own
 * UI Engine (Singleton per app).
 *
 * @author tomyeh
 */
public interface UiEngine {
	/** Starts the engine.
	 */
	public void start(WebApp wapp);
	/** Stops the engine.
	 * Called only if the server is about to stop.
	 */
	public void stop(WebApp wapp);

	/** Called when a desktop is being removed.
	 * <p>Application developers don't need to remove pages and desktops.
	 * They are removed and cleaned up automatically.
	 */
	public void desktopDestroyed(Desktop desktop);

	//-- update (draw) --//
	/** Called before a component redraws itself if the component might
	 * include another page.
	 *
	 * <p>If a new page is created, the specified component will become
	 * the owner of the new page.
	 *
	 * <p>It must reset the owner in the finally clause.
	 * <pre><code>old = ue.setOwner(this);
	 *try{
	 *  ...
	 *} finally {
	 *  ue.setOwner(old);
	 *}</code></pre>
	 * @return the previous owner
	 * @since 5.0.0
	 */
	public Component setOwner(Component comp);

	/** Returns if this component needs to be redrawn.
	 * <p>Note:
	 * <ol>
	 * <li>It always returns true if the current execution is not an
	 * asynchroous update.</li>
	 * <li>If its parent is invalidated, this component will be redrawn
	 * too, but this method returns false since {@link #addInvalidate(Component)}
	 * was not called against this component.</li>
	 * </ol>
	 * @since 3.0.5
	 */
	public boolean isInvalidated(Component comp);
	/** Invalidates the page to cause all of its components to redraw.
	 */
	public void addInvalidate(Page page);
	/** Invalidates a component to cause redrawing.
	 * Called when {@link Component#invalidate} is called.
	 */
	public void addInvalidate(Component comp);
	/** @deprecated As of release 5.0.2, replaced with {@link #addSmartUpdate(Component comp, String, Object, boolean)}.
	 */
	public void addSmartUpdate(Component comp, String attr, Object value);
	/** Smart-updates a property of the peer widget.
	 *
	 * @param append whether to append the updates of properties with the same
	 * name. If false, only the last value of the same property will be sent
	 * to the client.
	 * @since 5.0.2
	 */
	public void addSmartUpdate(Component comp, String attr, Object value, boolean append);
	/** Adds a response directly by using {@link AuResponse#getOverrideKey}
	 * as the override key.
	 * In other words, it is the same as <code>addResponse(resposne.getOverrideKey(), response)</code>
	 *
	 * <p>If the response is component-dependent, {@link AuResponse#getDepends}
	 * must return a component. And, if the component is removed, the response
	 * is removed, too.
	 * @since 5.0.2
	 * @see #addResponse(String, AuResponse)
	 */
	public void addResponse(AuResponse response);
	/** Adds a response which will be sent to client at the end
	 * of the execution.
	 * Called by {@link org.zkoss.zk.ui.AbstractComponent#response}.
	 *
	 * <p>Note: {@link Execution#addAuResponse} is a shortcut to this method,
	 * and it is used by application developers.
	 *
	 * <p>If {@link AuResponse#getDepends} is not null, the response
	 * depends on the returned componet. In other words, the response
	 * is removed if the component is removed.
	 * If it is null, the response is component-independent.
	 *
	 * @param key could be anything. If null, the response is appended.
	 * If not null, the second invocation of this method
	 * in the same execution with the same key and the same depends ({@link AuResponse#getDepends})
	 * will override the previous one.
	 * @see #addResponse(AuResponse)
	 */
	public void addResponse(String key, AuResponse response);
	/** Called to update (redraw) a component, when a component is moved.
	 * If a component's page or parent is changed, this method need to be
	 * called only once for the top one.
	 *
	 * @param oldparent the parent before moved
	 * @param oldpg the page before moved
	 * @param newpg the page after moved
	 */
	public void addMoved(Component comp, Component oldparent, Page oldpg, Page newpg);
	/** Called before changing the component's UUID.
	 * @since 5.0.3
	 */
	public void addUuidChanged(Component comp);

	//-- execution --//
	/** Creates components specified in the given page definition.
	 * Called when a new page is creates.
	 */
	public void execNewPage(Execution exec, PageDefinition pagedef, Page page,
	Writer out) throws IOException;
	/** Invoke {@link Richlet#service}, when a new page is creates upon
	 * visiting a richlet.
	 */
	public void execNewPage(Execution exec, Richlet richlet, Page page,
	Writer out) throws IOException;

	/** Reuse the desktop and generate the outout.
	 * @since 5.0.0
	 */
	public void recycleDesktop(Execution exec, Page page, Writer out)
	throws IOException;

	/** Executs an asynchronous update to a component (or page).
	 * It is the same as execUpdate(exec, requests, null, out).
	 *
	 * <p>Note: the output must be XML and UTF-8.
	 *
	 * @param requests a list of {@link org.zkoss.zk.au.AuRequest}.
	 */
	public void execUpdate(Execution exec, List requests, AuWriter out)
	throws IOException;

	/** Activates an execution that will allow developers to update
	 * the state of components.
	 * <p>It is designed to implement {@link org.zkoss.zkplus.embed.Bridge}.
	 *
	 * @return a context that shall be passed to {@link #finishUpdate}.
	 * @since 5.0.5
	 * @see #finishUpdate
	 * @see #closeUpdate
	 */
	public Object startUpdate(Execution exec) throws IOException;
	/** Finishes the update and returns the result in an array of JSON object.
	 * Notice it does not deactivate the execution. Rather, the caller
	 * has to invoke {@link #closeUpdate}.
	 * <p>It is designed to implement {@link org.zkoss.zkplus.embed.Bridge}.
	 *
	 * @param ctx the context returned by the previous call to {@link #startUpdate}
	 * @since 5.0.5
	 * @see #startUpdate
	 * @see #closeUpdate
	 */
	public JSONArray finishUpdate(Object ctx) throws IOException;
	/** Deactivates the execution and cleans up.
	 * <p>It is designed to implement {@link org.zkoss.zkplus.embed.Bridge}.
	 * @since 5.0.5
	 * @see #startUpdate
	 * @see #finishUpdate
	 */
	public void closeUpdate(Object ctx) throws IOException;

	/** Executes the recovering.
	 */
	public void execRecover(Execution exec, FailoverManager failover);

	/** Creates components from the specified page and definition.
	 * It can be called when {@link #execNewPage} or {@link #execUpdate}
	 * was called.
	 * It assumes the execution is already locked to this desktop.
	 *
	 * <p>Note: if both page and parent are null, the created components
	 * don't belong to any page/parent.
	 *
	 * @param exec the execution (never null).
	 * @param pagedef the page definition (never null).
	 * @param page the page. Ignored if parent is specified and
	 * parent's page is not null (parent's page will be used).
	 * If both page and parent are null, the created components won't belong
	 * to any page.
	 * @param parent the parent component, or null if no parent compoent.
	 * If parent is specified, page is ignored.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @return the components being created.
	 */
	public Component[] createComponents(Execution exec,
	PageDefinition pagedef, Page page, Component parent, Map arg);

	/** Sends a temporary redirect response to the client using the specified
	 * redirect location URL.
	 *
	 * <p>After calling this method, the caller shall end the processing
	 * immediately (by returning). All pending requests and events will
	 * be dropped.
	 *
	 * @param uri the URI to redirect to, or null to reload the same page
	 * @param target the new target, or null to denote the same browser window
	 */
	public void sendRedirect(String uri, String target);
	/** Aborts the current execution.
	 * if not null, it means the current execution is aborting
	 *
	 * <p>Note: if setAbortingReason is ever set with non-null, you
	 * CANNOT set it back to null.
	 *
	 * <p>After call this method, you shall not keep processing the page
	 * because the rendering is dropped and the client is out-of-sync
	 * with the server.
	 *
	 * @param aborting the aborting reason.
	 */
	public void setAbortingReason(AbortingReason aborting);

	//-- wait/notify --//
	/** Suspends the current processing of an event and wait until the
	 * other thread invokes {@link #notify(Object)}, {@link #notifyAll(Object)},
	 * {@link #notify(Desktop, Object)} or {@link #notifyAll(Desktop, Object)}
	 * for the specified object.
	 *
	 * <p>It can only be called when the current thread is processing an event.
	 * And, when called, the current processing is suspended and ZK continues
	 * to process the next event and finally render the result.
	 *
	 * <p>It is typical use to implement a modal dialog where it won't return
	 * until the modal dialog ends.
	 *
	 * @param obj any non-null object to identify what to wait, such that
	 * {@link #notify(Object)} and {@link #notify(Desktop, Object)} knows
	 * which object to notify.
	 * @exception UiException if it is called not during event processing.
	 * @exception SuspendNotAllowedException if there are too many suspended
	 * exceptions.
	 * Deployers can control the maximal allowed number of suspended exceptions
	 * by specifying <code>max-suspended-thread</code> in <code>zk.xml</code>,
	 * or invoking {@link org.zkoss.zk.ui.util.Configuration#setMaxSuspendedThreads}.
	 */
	public void wait(Object obj)
	throws InterruptedException, SuspendNotAllowedException;
	/** Wakes up a single event processing thread that is waiting on the
	 * specified object.
	 *
	 * <p>Unlike {@link #notify(Desktop, Object)}, this method can be invoked only
	 * if the same desktop is locked for processing requests.
	 *
	 * @param obj any non-null object to identify what to notify. It must be
	 * same object passed to {@link #wait}.
	 * @see #notify(Desktop, Object)
	 * @see #notifyAll(Object)
	 * @exception UiException if it is called not during event processing.
	 */
	public void notify(Object obj);
	/** Wakes up all event processing thread that are waiting on the
	 * specified object.
	 *
	 * <p>Unlike {@link #notify(Desktop, Object)}, this method can be invoked only
	 * if the same desktop is locked for processing requests.
	 *
	 * @param obj any non-null object to identify what to notify. It must be
	 * same object passed to {@link #wait}.
	 * @see #notify(Desktop, Object)
	 * @see #notifyAll(Object)
	 * @exception UiException if it is called not during event processing.
	 */
	public void notifyAll(Object obj);
	/** Wakes up a single event processing thread for the specified desktop
	 * that is waiting on the specified object.
	 *
	 * <p>Unlike {@link #notify(Object)}, this method can be called any time.
	 * It is designed to let working threads resume an event processing
	 * thread.
	 *
	 * <p>Notice: if this method is NOT called in an event processing thread,
	 * the resumed thread won't execute until the next request is received.
	 * To enforce it happen, you might use the timer component (found in ZUL).
	 *
	 * @param desktop the desktop which the suspended thread is processing.
	 * It must be the same desktop of the suspended thread.
	 * @param obj any non-null object to identify what to notify. It must be
	 * same object passed to {@link #wait}.
	 * @see #notify(Object)
	 * @see #notifyAll(Desktop, Object)
	 */
	public void notify(Desktop desktop, Object obj);
	/** Wakes up all event processing theads for the specified desktop
	 * that are waiting on the specified object.
	 *
	 * <p>Unlike {@link #notifyAll(Object)}, this method can be called any time.
	 * It is designed to let working threads resume an event processing
	 * thread.
	 *
	 * <p>If this method is NOT called in an event processing thread,
	 * the resumed thread won't execute until the next request is received.
	 * To enforce it happen, you might use the timer component (found in ZUL).
	 *
	 * @param desktop the desktop which the suspended thread is processing.
	 * It must be the same desktop of the suspended thread.
	 * @param obj any non-null object to identify what to notify. It must be
	 * same object passed to {@link #wait}.
	 * @see #notify(Object)
	 * @see #notifyAll(Desktop, Object)
	 */
	public void notifyAll(Desktop desktop, Object obj);

	/** Activates an execution such that you can access a component.
	 * You must call {@link #deactivate} in the finally clause.
	 *
	 * <p>Note: you RARELY need to invoke this method because {@link #execNewPage}
	 * and {@link #execUpdate} will activate and deactivate automatically.
	 *
	 * <p>Note: this method can be called only when processing a client request
	 * (e.g., HTTP) other than creating a new page and processing async-update.
	 *
	 * <p>Also, even if you use this method to grant the right to access
	 * components of the specified page, don't post events, create, remove,
	 * invalidate and do any smart updates. In other words, READ ONLY.
	 */
	public void activate(Execution exec);
	/** Deactivates an execution, such that other threads could activate
	 * and access components.
	 */
	public void deactivate(Execution exec);

	/** Activates and prepare for asynchronous update
	 * @since 3.5.0
	 */
	public void beginUpdate(Execution exec);
	/** Executes posted events, deactive and ends the asynchronous update.
	 *
	 * @since 5.0.0
	 */
	public void endUpdate(Execution exec)
	throws IOException;

	/** Retrieve the native content for a property of the specified component.
	 * The native content is a value of a property that is represented
	 * by a XML fragment (actually {@link org.zkoss.zk.ui.metainfo.NativeInfo}).
	 *
	 * <p>Example:
	 * <pre><code>&lt;html&gt;
	 * &lt;attribute name="content"&gt;
	 *  &lt;br/&gt;
	 * &lt;/attribute&gt;
	 *&lt;/html&gt;</code></pre>
	 *
	 * @param comp the component that the native content will be assigned to.
	 * It is the object that the self variable is assigned when evaluating
	 * EL expressions.
	 * @param children a list of {@link org.zkoss.zk.ui.metainfo.NativeInfo},
	 * {@link org.zkoss.zk.ui.metainfo.TextInfo} and others.
	 * This method evaluates them one-by-one and returns the result
	 * which is the value that will be assigned.
	 * @param helper the helper used to generate the content.
	 * @see org.zkoss.zk.ui.metainfo.Property
	 * @since 3.5.0
	 */
	public String getNativeContent(Component comp, List children,
	Native.Helper helper);

	/** Returns if any suspended event processing thread in the
	 * whole system.
	 */
	public boolean hasSuspendedThread();
	/** Returns a collection of suspended event processing threads
	 * belonging to the specified desktop,
	 * or empty if no suspended thread at all.
	 *
	 * <p>An event processing thread is an instance of
	 * {@link EventProcessingThread}
	 *
	 * @param desktop the desktop that the suspended event processing
	 * threads belong to (never null).
	 */
	public Collection getSuspendedThreads(Desktop desktop);
	/** Ceases the specified event thread.
	 *
	 * @param desktop which desktop the event thread belongs to
	 * @param cause an arbitrary text to describe the cause.
	 * It will be the message of the thrown InterruptedException.
	 * @return true if the event processing thread is ceased successfully;
	 * false if no such thread or it is not suspended.
	 */
	public boolean ceaseSuspendedThread(
		Desktop desktop, EventProcessingThread evtthd, String cause);

	/** Sets whether to disable the update of the client widget.
	 * By default, if a component is attached to a page, modications that
	 * change the visual representation will be sent to the client to
	 * ensure the consistency.
	 *
	 * @return whether it has been disabled before this invocation, i.e.,
	 * the previous disable status
	 * @since 3.6.2
	 */
	public boolean disableClientUpdate(Component comp, boolean disable);
}
