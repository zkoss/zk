/* I18Ns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 13 16:53:47     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import org.zkoss.util.TimeZones;
import org.zkoss.util.logging.Log;
import org.zkoss.web.Attributes;
import org.zkoss.web.servlet.Charsets;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.sys.SessionsCtrl;

/**
 * Internatization utilities.
 *
 * <p>Typical use:
 * <pre><code>
 Object old = I18Ns.setup(session, request, response);
 try {
   ...
 } finally {
   I18Ns.cleanup(old);
 }
 *</code></pre>
 * @author tomyeh
 */
public class I18Ns {
	private static final Log log = Log.lookup(I18Ns.class);
	private static final String ATTR_SETUP = "org.zkoss.zk.ui.http.charset.setup";

	/** Sets up the internationalization attributes, inclluding locale
	 * and time zone.
	 *
	 * @param sess the session. It cannot be null.
	 * @param charset the response's charset. If null or empty,
	 * response.setCharacterEncoding won't be called, i.e., the container's
	 * default is used.
	 */
	public static final Object setup(Session sess,
	ServletRequest request, ServletResponse response, String charset) {
		return setup((Object)sess, request, response, charset);
	}
	/** Sets up the internationalization attributes, inclluding locale
	 * and time zone.
	 *
	 * <p>This method is used only for requests that don't
	 * count on {@link Session}.
	 *
	 * @param sess the HTTP session. It cannot be null.
	 * @param charset the response's charset. If null or empty,
	 * response.setCharacterEncoding won't be called, i.e., the container's
	 * default is used.
	 * @since 3.6.2
	 */
	public static final Object setup(HttpSession sess,
	ServletRequest request, ServletResponse response, String charset) {
		return setup((Object)sess, request, response, charset);
	}
	private static final Object setup(Object sess,
	ServletRequest request, ServletResponse response, String charset) {
		final Object[] old;
		if (request.getAttribute(ATTR_SETUP) != null) { //has been setup
			old = null;
		} else {
			final HttpSession hsess;
			if (sess instanceof Session) {
				final Session se = (Session)sess;
				//Invoke the request interceptors
				se.getWebApp().getConfiguration()
					.invokeRequestInterceptors(se, request, response);

				hsess = (HttpSession)se.getNativeSession();
			} else {
				hsess = (HttpSession)sess;
			}

			final Object ol = Charsets.setup(hsess, request, response, charset);
				//Charsets will handle PREFERRED_LOCALE

			Object tz = hsess.getAttribute(Attributes.PREFERRED_TIME_ZONE);
			if (tz != null && !(tz instanceof TimeZone)) {
				log.warning(Attributes.PREFERRED_TIME_ZONE+" ignored. TimeZone is required, not "+tz.getClass());
				tz = null;
			}
			final Object otz = TimeZones.setThreadLocal((TimeZone)tz);

			request.setAttribute(ATTR_SETUP, Boolean.TRUE); //mark as setup
			old = new Object[] {ol, otz};
		}

		if (sess instanceof Session)
			SessionsCtrl.setCurrent((Session)sess);
		return old;
	}
	/* Cleans up the inernationalization attributes.
	 *
	 * @param old which must be the value returned by setup.
	 */
	public static final void cleanup(ServletRequest request, Object old) {
		if (old != null) {
			request.removeAttribute(ATTR_SETUP);

			SessionsCtrl.setCurrent(null);

			final Object[] op = (Object[])old;
			TimeZones.setThreadLocal((TimeZone)op[1]);
			Charsets.cleanup(request, op[0]);
		}
	}
}
