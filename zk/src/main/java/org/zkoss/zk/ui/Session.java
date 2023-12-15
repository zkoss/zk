/* Session.java

	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:29:17     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Map;

import org.zkoss.zk.ui.ext.Scope;

/**
 * A user session.
 *
 * <p>To get the current session, use {@link Sessions#getCurrent},
 * or {@link Desktop#getSession}.
 *
 * <p>A session, {@link Session}, might have multiple pages,
 * {@link Page}, while a page belongs to exactly one session.
 * A page, {@link Page}, might have many components, {@link Component}, while
 * a component belongs to exactly one page.
 *
 * @author tomyeh
 */
public interface Session extends Scope {
	/** Returns the device type that this session belongs to.
	 *
	 * <p>A device type identifies the type of a client. For example, "ajax"
	 * represents the Web browsers with Ajax support,
	 * while "mil" represents clients that supports
	 * <i>Mobile Interactive markup Language</i> (on Limited Connected Device,
	 * such as mobile phones).
	 *
	 * <p>All desktops of the same session must belong to the same
	 * device type.
	 *
	 * <p>The session's device type is determined by the first desktop's
	 * device type.
	 *
	 * @since 2.4.1
	 */
	public String getDeviceType();

	/** Returns the value of the specified custom attribute.
	 */
	public Object getAttribute(String name);

	/** Sets the value of the specified custom attribute.
	 * @return the previous value if any (since ZK 5)
	 */
	public Object setAttribute(String name, Object value);

	/** Removes the specified custom attribute.
	 * @return the previous value if any (since ZK 5)
	 */
	public Object removeAttribute(String name);

	/** Returns a map of custom attributes associated with this session.
	 */
	public Map<String, Object> getAttributes();

	/** Returns the Web application that this session belongs to.
	 */
	public WebApp getWebApp();

	/** Invalidates this session then unbinds any objects bound to it.
	 *
	 * <p>Note: you usually have to ask the client to redirect to another page
	 * (or reload the same page) by use of {@link Executions#sendRedirect}.
	 *
	 * <p>The session is not invalidated immediately. Rather, it is
	 * invalidated after processing the current request.
	 */
	public void invalidate();

	/** Specifies the time, in seconds, between client requests before
	 * the servlet container will invalidate this session.
	 * A negative time indicates the session should never timeout.
	 *
	 * @see org.zkoss.zk.ui.util.Configuration#setTimerKeepAlive
	 * @see org.zkoss.zk.ui.util.Configuration#setSessionMaxInactiveInterval
	 */
	public void setMaxInactiveInterval(int interval);

	/** Return the time, in seconds, between client requests before
	 * the servlet container will invalidate this session.
	 * A negative time indicates the session should never timeout.
	 *
	 * @see org.zkoss.zk.ui.util.Configuration#isTimerKeepAlive
	 * @see org.zkoss.zk.ui.util.Configuration#getSessionMaxInactiveInterval
	 * @since 3.0.0
	 */
	public int getMaxInactiveInterval();

	/** Returns the native session, or null if not available.
	 * The returned object depends on the type of clients.
	 * If HTTP, the object is an instance of javax.servlet.http.HttpSession.
	 * If portlet, the object is an instance of javax.portlet.PortletSession.
	 */
	public Object getNativeSession();
}
