/* Attributes.java

{{IS_NOTE
	$Id: Attributes.java,v 1.12 2006/02/27 03:54:26 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Nov 17 15:05:17     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web;

/**
 * Definition of contstants used as attributes and parameters acrossing
 * requests.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.12 $ $Date: 2006/02/27 03:54:26 $
 */
public class Attributes {
	/** Don't construct it. All members are static. */
	protected Attributes() {}

	/** The title (String) of a servlet or a portal.
	 * Stored as an attribute of the request.
	 */
	public static final String TITLE = "px_title";

	/** Te attribute used to store the preferred locale (Locale) in a session.
	 */
	public static final String PREFERRED_LOCALE = "px_preferred_locale";

	/** The attribute name that contains the alert passed from
	 * another request {@link com.potix.web.servlet.http.Https#sendRedirect}.
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
	 * with {@link com.potix.web.servlet.Servlets#PASS_THRU_ATTR}.
	 */
	public static final String ARG = "arg";
}
