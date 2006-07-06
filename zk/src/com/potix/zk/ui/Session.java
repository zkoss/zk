/* Session.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:29:17     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui;

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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Session {
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
	 * <p>The session is not invalidated immediately. Rather, it is
	 * invalidated after processing the current request.
	 */
	public void invalidate();
	/** Specifies the time, in seconds, between client requests before
	 * the servlet container will invalidate this session.
	 * A negative time indicates the session should never timeout.
	 */
	public void setMaxInactiveInterval(int interval);

	/** Returns the native session, or null if not available.
	 * The returned object depends on the type of clients.
	 * If the client is a browser, the object is an instance of
	 * javax.servlet.http.HttpSession.
	 */
	public Object getNativeSession();
}
