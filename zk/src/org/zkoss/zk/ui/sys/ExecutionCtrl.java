/* ExecutionCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 14:36:47     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.io.Writer;
import java.util.Collection;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.metainfo.PageDefinition;

/**
 * Additional interface to {@link org.zkoss.zk.ui.Execution}
 * for implementation.
 *
 * <p>Application developers shall never access any of this methods.
 *
 * @author tomyeh
 */
public interface ExecutionCtrl {
	/** Returns the current page.
	 * Though an execution might process many pages, it processes update
	 * requests one-by-one and each update request is associated
	 * with a page.
	 *
	 * <p>Design decision: we put it here because user need not to know
	 * about the conccept of the current page.
	 *
	 * <p>Since 3.5.3, this method returns the first page if
	 * {@link #setCurrentPage} was not called (such as Server Push).
	 *
	 * @see Desktop#getPage
	 */
	public Page getCurrentPage();
	/** Sets the current page.
	 * Though an execution might process many pages, it processes update requests
	 * one-by-one and each update request is associated with a page.
	 */
	public void setCurrentPage(Page page);

	/** Returns the current page definition, which is pushed when
	 * evaluating a page (from a page definition).
	 */
	public PageDefinition getCurrentPageDefinition();
	/** Sets the current page definition.
	 * @param pgdef the page definition. If null, it means it is the same
	 * as getCurrentPage().getPageDefinition().
	 */
	public void setCurrentPageDefinition(PageDefinition pgdef);

	/** Returns the next event queued by
	 * {@link org.zkoss.zk.ui.Execution#postEvent}, or null if no event queued.
	 */
	public Event getNextEvent();

	/** Returns whether this execution is activated.
	 */
	public boolean isActivated();
	/** Called when this execution is about to become the current execution
	 * {@link org.zkoss.zk.ui.Executions#getCurrent}.
	 *
	 * <p>Note: an execution might spread over several threads, so
	 * this method might be called several times to activate the states
	 * in each thread. Also, an execution might be activated before another
	 * is deactivate. For example, when a component includes another page,
	 * the second exec is activated to render the included page.
	 *
	 * <p>It is used as callback notification.
	 *
	 * <p>Note: don't throw any exception in this method.
	 */
	public void onActivate();
	/** Called when this execution is about to become a non-current execution.
	 *
	 * <p>It is used as callback notification.
	 *
	 * <p>Note: don't throw any exception in this method.
	 *
	 * @see #onActivate
	 */
	public void onDeactivate();

	/** Returns whether this execution is in recovering.
	 * In other words, it is in the invocation of {@link FailoverManager#recover}.
	 * If in recovering, no response is sent to the client.
	 * It assumes the server is recovering the desktop and all it contains
	 * to match the client's status.
	 */
	public boolean isRecovering();

	/** Sets the {@link Visualizer} for this execution.
	 * It could be anything that {@link UiEngine} requires.
	 */
	public void setVisualizer(Visualizer ei);
	/** Returns the {@link Visualizer} for this execution
	 * (set by {@link #setVisualizer}.
	 */
	public Visualizer getVisualizer();

	/** Sets a responseheader with the given name and value.
	 *
	 * @param value the additional header value If it contains octet string,
	 * it should be encoded according to RFC 2047
	 * (http://www.ietf.org/rfc/rfc2047.txt).
	 */
	public void setHeader(String name, String value);
	/** Sets a response header with the given name and date-value.
	 * The date is specified in terms of milliseconds since the epoch.
	 * This method allows response headers to have multiple values.
	 * @since 3.0.0
	 */
	public void setDateHeader(String name, long value);
	/** Adds a responseheader with the given name and value.
	 *
	 * @param value the additional header value If it contains octet string,
	 * it should be encoded according to RFC 2047
	 * (http://www.ietf.org/rfc/rfc2047.txt).
	 * @since 3.0.0
	 */
	public void addHeader(String name, String value);
	/** Adds a response header with the given name and date-value.
	 * The date is specified in terms of milliseconds since the epoch.
	 * This method allows response headers to have multiple values.
	 * @since 3.0.0
	 */
	public void addDateHeader(String name, long value);

	/** Sets the content type.
	 * @since 5.0.0
	 */
	public void setContentType(String contentType);

	/** @deprecated As of release 3.0.7, replaced with {@link org.zkoss.zk.ui.Execution#getAttribute}.
	 */
	public Object getRequestAttribute(String name);
	/** @deprecated As of release 3.0.7, replaced with {@link org.zkoss.zk.ui.Execution#setAttribute}.
	 */
	public void setRequestAttribute(String name, Object value);

	/** Sets the desktop associated with this execution.
	 * You rarely need to use this method, since the desktop is associated
	 * when this execution is created.
	 *
	 * <p>Currently, it is used to communicate between WebManager.newDesktop
	 * and DesktopImpl's constructor.
	 *
	 * @exception IllegalArgumentException if desktop is null
	 * @exception IllegalStateException if there is already a desktop
	 * is associated with it.
	 * @since 3.0.0
	 */
	public void setDesktop(Desktop desktop);

	/** Sets the sequence ID of the current request.
	 * @since 3.0.5
	 */
	public void setRequestId(String reqId);
	/** Returns the sequence ID of the current request, or null if not
	 * available. Not all clients support the request ID.
	 * @since 3.0.5
	 */
	public String getRequestId();

	/** Returns the collection of the AU responses ({@link org.zkoss.zk.au.AuResponse})
	 * that shall be generated to the output, or null if not available.
	 * @since 5.0.0
	 */
	public Collection getResponses();
	/** Sets the collection of the AU responses ({@link org.zkoss.zk.au.AuResponse})
	 * that shall be generated to the output.
	 * @since 5.0.0
	 */
	public void setResponses(Collection responses);
}
