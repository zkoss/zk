/* Session.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:29:17     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Map;

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
public interface Session {
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
	 */
	public void setAttribute(String name, Object value);
	/** Removes the specified custom attribute.
	 */
	public void removeAttribute(String name);

	/** Returns a map of custom attributes associated with this session.
	 */
	public Map getAttributes();

	/** Returns the Web application that this session belongs to.
	 */
	public WebApp getWebApp();

	/** Returns the Internet Protocol (IP) address of the client that creates
	 * this session. 
	 */
	public String getClientAddr();
	/** Returns the fully qualified name of the client that creates
	 * this session. If the engine cannot or chooses not to resolve the
	 * hostname, this method returns the dotted-string form of the IP address.
	 */
	public String getClientHost();

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
	 * @see #setTimerAsInactive
	 * @see org.zkoss.zk.ui.util.Configuration#setSessionMaxInactiveInterval
	 */
	public void setMaxInactiveInterval(int interval);
	/** Return the time, in seconds, between client requests before
	 * the servlet container will invalidate this session.
	 * A negative time indicates the session should never timeout.
	 *
	 * @see #isTimerAsInactive
	 * @see org.zkoss.zk.ui.util.Configuration#getSessionMaxInactiveInterval
	 * @since 2.5.0
	 */
	public int getMaxInactiveInterval();

	/** Specifies whether <i>not</i> to reset the session timeout counter,
	 * when receiving the onTimer event.
	 * A session is expired (and then invalidated), if it didn't receive any
	 * client request in the specified timeout interval
	 * ({@link #getMaxInactiveInterval}).
	 * This method controls whether to ignore the onTimer event
	 * regarding the session timeout.
	 *
	 * <p>Note: if false (default) and the timer is shorter than
	 * the session timeout ({@link #getMaxInactiveInterval}),
	 * the session is never expired.
	 *
	 * <p>Default: false.
	 *
	 * @see org.zkoss.zk.ui.util.Configuration#setTimerAsInactive
	 * @since 2.5.0
	 */
	public void setTimerAsInactive(boolean asInactive);
	/** Returns whether <i>not</i> to reset the session timer counter,
	 * when receiving the onTimer event
	 *
	 * @see org.zkoss.zk.ui.util.Configuration#isTimerAsInactive
	 * @since 2.5.0
	 */
	public boolean isTimerAsInactive();

	/** Returns the native session, or null if not available.
	 * The returned object depends on the type of clients.
	 * If the client is a browser, the object is an instance of
	 * javax.servlet.http.HttpSession.
	 */
	public Object getNativeSession();
}
