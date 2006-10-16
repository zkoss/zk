/* UiEngine.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  9 12:58:20     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;

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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface UiEngine {
	/** Starts the engine.
	 */
	public void start(WebApp wapp);
	/** Stops the engine.
	 * Called only if the server is about to stop.
	 */
	public void stop(WebApp wapp);

	/** Cleans up a desktop before really removing it.
	 * Developers don't need to remove pages and desktops.
	 * They are removed and cleaned up automatically.
	 */
	public void cleanup(Desktop desktop);

	//-- update (draw) --//
	/** Called before a component redraws itself if the component might
	 * include another page.
	 *
	 * <p>If a new page is created, the specified component will become
	 * the owner of the new page.
	 */
	public void pushOwner(Component comp);
	/** Called after a component redraws itself if it ever calls
	 * {@link #pushOwner}.
	 */
	public void popOwner();

	/** Invalidates the page to cause all of its components to redraw.
	 */
	public void addInvalidate(Page page);
	/** Invalidates a component to cause redrawing.
	 * Called when {@link Component#invalidate} is called.
	 */
	public void addInvalidate(Component comp);
	/** Smart updates an attribute of a component.
	 * Called when {@link Component#smartUpdate(String,String)} is called.
	 *
	 * <p>The second invocation of this method
	 * in the same execution with the same attr will override the previous one.
	 */
	public void addSmartUpdate(Component comp, String attr, String value);
	/** Adds a response which will be sent to client at the end
	 * of the execution.
	 * Called when {@link Component#response} is called.
	 *
	 * <p>Note: {@link Execution#addAuResponse} is a shortcut to this method,
	 * and it is used by application developers.
	 *
	 * <p>If {@link AuResponse#getDepends} is not null, the response
	 * depends on the returned componet. In other words, the response
	 * is removed if the component is removed.
	 * If it is null, the response is component-independent.
	 *
	 * @param key could be anything. The second invocation of this method
	 * in the same execution with the same key will override the previous one.
	 */
	public void addResponse(String key, AuResponse response);
	/** Called to update (redraw) a component, when a component is moved.
	 * If a component's page or parent is changed, this method need to be
	 * called only once for the top one.
	 *
	 * @param oldparent the parent before moved
	 * @param newAttached whether the component is added to a page
	 * first time.
	 */
	public void addMoved(Component comp, Component oldparent, boolean newAttached);
	/** Called before changing the component's UUID.
	 *
	 * @param addOnlyMoved if true, it is added only if it was moved
	 * before (see {@link #addMoved}).
	 */
	public void addUuidChanged(Component comp, boolean addOnlyMoved);

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

	/** Executs an asynchronous update to a component (or page).
	 * <p>Note: the output must be XML and UTF-8.
	 */
	public void execUpdate(Execution exec, List requests, Writer out)
	throws IOException;
	/** Generates the output for the specified the response.
	 * <p>Note: the output must be XML and UTF-8.
	 */
	public void response(AuResponse response, Writer out)
	throws IOException;
	/** Generates the output of a list of responses.
	 * <p>Note: the output must be XML and UTF-8.
	 */
	public void response(List responses, Writer out)
	throws IOException;

	/** Creates components from the specified page and definition.
	 * It can be called when {@link #execNewPage} or {@link #execUpdate}
	 * was called.
	 * It assumes the execution is already locked to this desktop.
	 *
	 * @param exec the execution (never null).
	 * @param pagedef the page definition (never null).
	 * @param page the page. Ignored if parent is specified (and
	 * parent's page is used).
	 * @param parent the parent component, or null if no parent compoent.
	 * If both parent and page are specified, page is ignored.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @return the first component being created.
	 */
	public Component createComponents(Execution exec,
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
	 */
	public void wait(Object obj) throws InterruptedException;
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
}
