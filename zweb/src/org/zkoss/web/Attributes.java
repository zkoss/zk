/* Attributes.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Nov 17 15:05:17     2004, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web;

/**
 * Definition of contstants used as attributes and parameters acrossing
 * requests.
 *
 * @author tomyeh
 */
public class Attributes {
	/** Don't construct it. All members are static. */
	protected Attributes() {}

	/** The title (String) of a servlet or a portal.
	 * Stored as an attribute of the request.
	 */
	public static final String TITLE = "px_title";

	/** The attribute used to store the preferred locale (Locale) in a session.
	 */
	public static final String PREFERRED_LOCALE = "px_preferred_locale";
	/** The attribute used to store the preferred time zone (TimeZone) in a session.
	 */
	public static final String PREFERRED_TIME_ZONE = "px_preferred_time_zone";

	/** The attribute name that contains the alert passed from
	 * another request {@link org.zkoss.web.servlet.http.Https#sendRedirect}.
	 * It is automatically reset after the next request is responsed.
	 * In JSP, web authors use
	 * &lt;c:object name="i.alert" scope="request"&gt; to access it.
	 */
	public static final String ALERT = "px_alert";
	/** Denote the type of {@link #ALERT}.
	 * It could be "error", "warning" and undefined.
	 * It is used by JSP page to do more accurate page layout.
	 */
	public static final String ALERT_TYPE = "px_alert_type";

	/** "j_domain" as part of the j_check_security request.
	 */
	public static final String J_DOMAIN = "j_domain";
	/** "j_remember_me" as part of the j_check_security request.
	 */
	public static final String J_REMEMBER_ME = "j_remember_me";

	/** The attribute used to pass parameters from the caller that invokes
	 * Servlets.include() or Servlets.forward()
	 * with {@link org.zkoss.web.servlet.Servlets#PASS_THRU_ATTR}.
	 */
	public static final String ARG = "arg";

	//-- Standard constants --//
	/** The included context path; set by the servlet container.
	 * @see org.zkoss.web.servlet.http.Https#getThisServletPath
	 * @see org.zkoss.web.servlet.http.Https#getOriginServletPath
	 */
	public static final String INCLUDE_CONTEXT_PATH
		= "javax.servlet.include.context_path";
	/** The included servlet path; set by the servlet container.
	 * @see org.zkoss.web.servlet.http.Https#getThisServletPath
	 * @see org.zkoss.web.servlet.http.Https#getOriginServletPath
	 */
	public static final String INCLUDE_SERVLET_PATH
		= "javax.servlet.include.servlet_path";
	/** The included request URI; set by the servlet container.
	 * @see org.zkoss.web.servlet.http.Https#getThisRequestURI
	 */
	public static final String INCLUDE_REQUEST_URI
		= "javax.servlet.include.request_uri";
	/** The included servlet path; set by the servlet container.
	 * @see org.zkoss.web.servlet.http.Https#getThisPathInfo
	 * @see org.zkoss.web.servlet.http.Https#getOriginPathInfo
	 */
	public static final String INCLUDE_PATH_INFO
		= "javax.servlet.include.path_info";
	/** The included servlet path; set by the servlet container.
	 * @see org.zkoss.web.servlet.http.Https#getThisQueryString
	 * @see org.zkoss.web.servlet.http.Https#getOriginQueryString
	 */
	public static final String INCLUDE_QUERY_STRING
		= "javax.servlet.include.query_string";

	/** The original context path that forwards this page; set by the servlet container.
	 * @see org.zkoss.web.servlet.http.Https#getThisServletPath
	 * @see org.zkoss.web.servlet.http.Https#getOriginServletPath
	 */
	public static final String FORWARD_CONTEXT_PATH
		= "javax.servlet.forward.context_path";
	/** The original servlet path that forwards this page; set by the servlet container.
	 * @see org.zkoss.web.servlet.http.Https#getThisServletPath
	 * @see org.zkoss.web.servlet.http.Https#getOriginServletPath
	 */
	public static final String FORWARD_SERVLET_PATH
		= "javax.servlet.forward.servlet_path";
	/** The original request URI that forwards this page; set by the servlet container.
	 * @see org.zkoss.web.servlet.http.Https#getThisRequestURI
	 */
	public static final String FORWARD_REQUEST_URI
		= "javax.servlet.forward.request_uri";
	/** The original servlet path that forwards this page; set by the servlet container.
	 * @see org.zkoss.web.servlet.http.Https#getThisPathInfo
	 * @see org.zkoss.web.servlet.http.Https#getOriginPathInfo
	 */
	public static final String FORWARD_PATH_INFO
		= "javax.servlet.forward.path_info";
	/** The original servlet path that forwards this page; set by the servlet container.
	 * @see org.zkoss.web.servlet.http.Https#getThisQueryString
	 * @see org.zkoss.web.servlet.http.Https#getOriginQueryString
	 */
	public static final String FORWARD_QUERY_STRING
		= "javax.servlet.forward.query_string";
	/** The attribute to hold the exception, if any.
	 */
	public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
}
