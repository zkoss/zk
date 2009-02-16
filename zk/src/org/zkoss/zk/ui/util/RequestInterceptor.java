/* RequestInterceptor.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May 25 23:39:35     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Session;

/**
 * Used to intercept the requests for the ZK Loader and Update Engine.
 * Developers usually use it to prepare the locale and time zone.
 *
 * <p>{@link #request} is called at very early stage, even before
 * setting the locale for the request.
 * Thus, you can configure the locale as follows:<br/>
 *   <code>sess.setAttribute({@link org.zkoss.web.Attributes#PREFERRED_LOCALE}, myLocale);</code>
 *
 * <p>By default, the locale is decided by the browser's preference.
 * However, you may want to store the previous locale in the cookie
 * if your application is HTTP. Another example, you may want to 
 * retrieve the locale from the database where your application stores
 * user's preferences.
 *
 * <p>Similarly, you can configure the time zone (that is used by
 * some components, such as Datebox) as follows:<br/>
 *   <code>sess.setAttribute({@link org.zkoss.web.Attributes#PREFERRED_TIME_ZONE}, myLocale);</code>
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * Then, an instance of the specified class is created.
 * </li>
 * <li>Each time ZK Loader or ZK Update Engine receives a request,
 * it first invoke {@link #request} against the instance created in
 * the previous step.</li>
 * </ol>
 *
 * <p>Note:
 * <ul>
 * <li>Unlike {@link ExecutionInit} and others listeners, the same instance of
 * {@link RequestInterceptor} is used for the whole application.
 * Thus, you have to make sure it can be accessed concurrently.</li>
 * </ul>
 *
 * <h3>Differences to {@link URIInterceptor}</h3>
 *
 * <p>{@link URIInterceptor} is called when retrieving a page definition
 * from an URI ({@link org.zkoss.zk.ui.metainfo.PageDefinitions#getPageDefinition}). It may or may not be caused by a client request.
 * On the other hand, {@link RequestInterceptor} is called
 * when ZK Loader or ZK Update Engine is receiving a client request.
 *
 * @author tomyeh
 * @see URIInterceptor
 */
public interface RequestInterceptor {
	/** Called before a request is processed by ZK Loader or ZK
	 * Update Engine.
	 *
	 * @param sess the session (never null)
	 * @param request the request (never null).
	 * It is javax.servlet.http.HttpServletRequest, if the applicaiton is
	 * HTTP based. A common use is to look for the cookies.
	 * @param response the response (never null).
	 * It is javax.servlet.http.HttpServletResponse, if the applicaiton is
	 * HTTP based. A common use is to store the cookies.
	 */
	public void request(Session sess, Object request, Object response);
}
