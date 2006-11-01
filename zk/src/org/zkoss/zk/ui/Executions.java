/* Executions.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 17:55:08     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Map;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.idom.Document;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.WebAppCtrl;

/**
 * Utilities to access {@link Execution}.
 *
 * @author tomyeh
 */
public class Executions {
	/** Stores the current {@link Execution}. */
	protected static final ThreadLocal _exec = new ThreadLocal();

	/** Returns the current execution.
	 */
	public static final Execution getCurrent() {
		return (Execution)_exec.get();
	}

	/** Evluates the specified expression by use of the current context
	 * ({@link #getCurrent}).
	 *
	 * <p>The function mapper is retrieved from component's page's function
	 * mapper ({@link Page#getFunctionMapper}).
	 * If null, the current page, if any, is used to retrieve
	 * the mapper.
	 *
	 * @param comp as the self variable (ignored if null)
	 */
	public static final Object evaluate(Component comp,
	String expr, Class expectedType) {
		return getCurrent().evaluate(comp, expr, expectedType);
	}
	/** Evluates the specified expression with the resolver of the current
	 * execution ({@link #getCurrent}).
	 *
	 * <p>The function mapper is retrieved from page's function
	 * mapper ({@link Page#getFunctionMapper}).
	 * If null, the current page, if any, is used to retrieve
	 * the mapper.
	 *
	 * @param page used as the self variable and to retrieve the function
	 * mapper if funmap is not defined. Ignored if null.
	 */
	public static final Object evaluate(Page page,
	String expr, Class expectedType) {
		return getCurrent().evaluate(page, expr, expectedType);
	}
	
	/** Encodes an URL.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link org.zkoss.web.servlet.Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 */
	public static final String encodeURL(String uri) {
		return getCurrent().encodeURL(uri);
	}

	/** Creates components from a page file specified by an URI.
	 * Shortcut to {@link Execution#createComponents(String, Component, Map)}.
	 *
	 * @param parent the parent component, or null if you want it to be
	 * a root component. If parent is null, the page is assumed to be
	 * the current page, which is determined by the execution context.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Component, Map)
	 */
	public static final Component createComponents(
	String uri, Component parent, Map arg) {
		return getCurrent().createComponents(uri, parent, arg);
	}
	/** Creates components based on the specified page definition.
	 * Shortcut to {@link Execution#createComponents(PageDefinition, Component, Map)}.
	 *
	 * @param pagedef the page definition to use. It cannot be null.
	 * @param parent the parent component, or null if you want it to be
	 * a root component. If parent is null, the page is assumed to be
	 * the current page, which is determined by the execution context.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @return the first component being created.
	 * @see #createComponents(String, Component, Map)
	 */
	public static final Component createComponents(PageDefinition pagedef,
	Component parent, Map arg) {
		return getCurrent().createComponents(pagedef, parent, arg);
	}

	/** Creates components from the raw content specified by a string.
	 * Shortcut to {@link Execution#createComponentsDirectly(String, String, Component, Map)}.
	 *
	 * @param content the raw content of the page. It must be a XML and
	 * compliant to the page format (such as ZUL).
	 * @param extension the default extension if the content doesn't specify
	 * an language. Ignored if null.
	 * If the content doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 * @param parent the parent component, or null if you want it to be
	 * a root component. If parent is null, the page is assumed to be
	 * the current page, which is determined by the execution context.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Component, Map)
	 * @see #createComponents(String, Component, Map)
	 * @see #createComponentsDirectly(Document, String, Component, Map)
	 * @see #createComponentsDirectly(Reader, String, Component, Map)
	 */
	public static final Component createComponentsDirectly(String content,
	String extension, Component parent, Map arg) {
		return getCurrent().createComponentsDirectly(content, extension, parent, arg);
	}
	/** Creates components from the raw content specified by a DOM tree.
	 * Shortcut to {@link Execution#createComponentsDirectly(Document, String, Component, Map)}.
	 *
	 * @param content the raw content in DOM.
	 * @param extension the default extension if the content doesn't specify
	 * an language. Ignored if null.
	 * If the content doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 * @param parent the parent component, or null if you want it to be
	 * a root component. If parent is null, the page is assumed to be
	 * the current page, which is determined by the execution context.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Component, Map)
	 * @see #createComponents(String, Component, Map)
	 * @see #createComponentsDirectly(String, String, Component, Map)
	 * @see #createComponentsDirectly(Reader, String, Component, Map)
	 */
	public static final Component createComponentsDirectly(Document content,
	String extension, Component parent, Map arg) {
		return getCurrent().createComponentsDirectly(content, extension, parent, arg);
	}
	/** Creates components from the raw content read from the specified reader.
	 * Shortcut to {@link Execution#createComponentsDirectly(Reader, String, Component, Map)}.
	 *
	 * <p>The raw content is loader and parsed to a page defintion by use of
	 * {@link Execution#getPageDefinitionDirectly(Reader, String)}, and then
	 * invokes {@link #createComponents(PageDefinition,Component,Map)}
	 * to create components.
	 *
	 * @param reader the reader to retrieve the raw content.
	 * @param extension the default extension if the content of reader doesn't specify
	 * an language. Ignored if null.
	 * If the content doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 * @param parent the parent component, or null if you want it to be
	 * a root component. If parent is null, the page is assumed to be
	 * the current page, which is determined by the execution context.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Component, Map)
	 * @see #createComponents(String, Component, Map)
	 * @see #createComponentsDirectly(Document, String, Component, Map)
	 * @see #createComponentsDirectly(String, String, Component, Map)
	 */
	public static Component createComponentsDirectly(Reader reader,
	String extension, Component parent, Map arg)
	throws IOException {
		return getCurrent().createComponentsDirectly(reader, extension, parent, arg);
	}

	/** Sends a temporary redirect response to the client using the specified
	 * redirect location URL by use of the current execution,
	 * {@link #getCurrent}.
	 *
	 * <p>After calling this method, the caller shall end the processing
	 * immediately (by returning). All pending requests and events will
	 * be dropped.
	 *
	 * @param uri the URI to redirect to, or null to reload the same page
	 * @see Execution#sendRedirect
	 */
	public static void sendRedirect(String uri) {
		getCurrent().sendRedirect(uri);
	}

	/** A shortcut of Executions.getCurrent().include(page).
	 *
	 * @see Execution#include(Writer,String,Map,int)
	 * @see Execution#include(String)
	 */
	public static void include(String page)
	throws IOException {
		getCurrent().include(page);
	}
	/** A shortcut of Executions.getCurrent().forward(page).
	 *
	 * @see Execution#forward(Writer,String,Map,int)
	 * @see Execution#forward(String)
	 */
	public static void forward(String page)
	throws IOException {
		getCurrent().forward(page);
	}

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
	 * @param mutex any non-null object to identify what to notify.
	 * It must be same object passed to {@link #notify(Desktop, Object)}.
	 * If there is racing issue, you have to enclose it with
	 * <code>synchronized</code> (though it is optional).
	 * @exception UiException if it is called not during event processing.
	 */
	public static final void wait(Object mutex)
	throws InterruptedException {
		getUiEngine().wait(mutex);
	}
	/** Wakes up a single event processing thread that is waiting on the
	 * specified object.
	 *
	 * <p>Unlike {@link #notify(Desktop, Object)}, this method can be invoked only
	 * in the event listener that processing the same desktop.
	 * In addition, this method can be called under the event listener.
	 *
	 * <p>Use {@link #notify(Desktop, Object)} if you want to notify in other
	 * thread, such as a working thread.
	 *
	 * @param mutex any non-null object to identify what to notify.
	 * It must be same object passed to {@link #wait}.
	 * If there is racing issue, you have to enclose it with
	 * <code>synchronized</code> (though it is optional).
	 * @see #notify(Desktop, Object)
	 * @see #notifyAll(Object)
	 * @exception UiException if it is called not during event processing.
	 */
	public static final void notify(Object mutex) {
		getUiEngine().notify(mutex);
	}
	/** Wakes up all event processing thread that are waiting on the
	 * specified object.
	 *
	 * <p>Unlike {@link #notify(Desktop, Object)}, this method can be invoked only
	 * in the event listener that processing the same desktop.
	 * In addition, this method can be called under the event listener.
	 *
	 * <p>Use {@link #notifyAll(Desktop, Object)} if you want to notify in other
	 * thread, such as a working thread.
	 *
	 * @param mutex any non-null object to identify what to notify.
	 * It must be same object passed to {@link #wait}.
	 * If there is racing issue, you have to enclose it with
	 * <code>synchronized</code> (though it is optional).
	 * @see #notify(Desktop, Object)
	 * @see #notifyAll(Object)
	 * @exception UiException if it is called not during event processing.
	 */
	public static final void notifyAll(Object mutex) {
		getUiEngine().notifyAll(mutex);
	}
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
	 * <p>Notice: to resolve racing issue, you usually need to follow
	 * this pattern.
	 * <pre><code>
//Event Handling Thread
synchronized (mutex) {
	final WorkingThread worker = new WorkingThread(desktop);
	synchronized (mutex) {
		worker.start();
		Executions.wait(mutex);
	}
	....
}
//Working Thread
public void run() {
	....
	synchronized (mutex) {
		Executions.notify(desktop, mutex);
	}
}
	 </code></pre>
	 *
	 * @param desktop the desktop which the suspended thread is processing.
	 * It must be the same desktop of the suspended thread.
	 * @param mutex any non-null object to identify what to notify.
	 * It must be same object passed to {@link #wait}.
	 * If there is racing issue, you have to enclose it with
	 * <code>synchronized</code> (though it is optional).
	 * @see #notify(Object)
	 * @see #notifyAll(Desktop, Object)
	 */
	public static final void notify(Desktop desktop, Object mutex) {
		getUiEngine(desktop).notify(desktop, mutex);
	}
	/** Wakes up all event processing theads for the specified desktop
	 * that are waiting on the specified object.
	 *
	 * <p>Unlike {@link #notifyAll(Object)}, this method can be called any time.
	 * It is designed to let working threads resume an event processing
	 * thread.
	 *
	 * <p>Notice: if this method is NOT called in an event processing thread,
	 * the resumed thread won't execute until the next request is received.
	 * To enforce it happen, you might use the timer component (found in ZUL).
	 *
	 * <p>Notice: to resolve racing issue, you usually need to follow
	 * this pattern.
	 * <pre><code>
//Event Handling Thread
synchronized (mutex) {
	final WorkingThread worker = new WorkingThread(desktop);
	synchronized (mutex) {
		worker.start();
		Executions.wait(mutex);
	}
	....
}
//Working Thread
public void run() {
	....
	synchronized (mutex) {
		Executions.notifyAll(desktop, mutex);
	}
}
	 </code></pre>
	 *
	 * @param desktop the desktop which the suspended thread is processing.
	 * It must be the same desktop of the suspended thread.
	 * @param mutex any non-null object to identify what to notify.
	 * It must be same object passed to {@link #wait}.
	 * If there is racing issue, you have to enclose it with
	 * <code>synchronized</code> (though it is optional).
	 * @see #notify(Object)
	 * @see #notifyAll(Desktop, Object)
	 */
	public static final void notifyAll(Desktop desktop, Object mutex) {
		getUiEngine(desktop).notifyAll(desktop, mutex);
	}

	private static final UiEngine getUiEngine(Desktop desktop) {
		if (desktop == null)
			throw new IllegalArgumentException("desktop cannot be null");
		return ((WebAppCtrl)desktop.getWebApp()).getUiEngine();
	}
	private static final UiEngine getUiEngine() {
		final Execution exec = getCurrent();
		if (exec == null)
			throw new IllegalStateException("This method can be called only under an event listener");
		return ((WebAppCtrl)exec.getDesktop().getWebApp()).getUiEngine();
	}
}
