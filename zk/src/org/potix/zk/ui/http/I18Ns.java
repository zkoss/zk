/* I18Ns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 13 16:53:47     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.http;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.potix.util.TimeZones;
import com.potix.util.logging.Log;
import com.potix.web.Attributes;
import com.potix.web.servlet.Charsets;

import com.potix.zk.ui.Session;
import com.potix.zk.ui.util.Configuration;
import com.potix.zk.ui.sys.LocaleProvider;
import com.potix.zk.ui.sys.TimeZoneProvider;
import com.potix.zk.ui.sys.SessionsCtrl;

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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class I18Ns {
	private static final Log log = Log.lookup(I18Ns.class);

	/** Sets up the internationalization attributes, inclluding locale
	 * and time zone.
	 * @param charset the response's charset. If null or empty,
	 * response.setCharacterEncoding won't be called, i.e., the container's
	 * default is used.
	 */
	public static final Object setup(Session sess,
	ServletRequest request, ServletResponse response, String charset) {
		final Object[] old;
		if (Charsets.hasSetup(request)) {
			old = null;
		} else {
			//1. setup locale
			final Configuration config = sess.getWebApp().getConfiguration();
			Class cls = config.getLocaleProviderClass();
			if (cls != null) {
				try {
					final Locale locale =
						((LocaleProvider)cls.newInstance()).getLocale(sess);
					if (locale != null)
						sess.setAttribute(Attributes.PREFERRED_LOCALE, locale);
						//so Charsets will use this locale
				} catch (Throwable ex) {
					log.warning("Ignored: unable to invoke the locale provider: "+cls, ex);
				}
			}
			final Object ol = Charsets.setup(request, response, charset);

			//2. setup time zone
			TimeZone tzone = null;
			cls = config.getTimeZoneProviderClass();
			if (cls != null) {
				try {
					tzone = ((TimeZoneProvider)cls.newInstance()).getTimeZone(sess);
				} catch (Throwable ex) {
					log.warning("Ignored: unable to invoke the locale provider: "+cls, ex);
				}
			}
			if (tzone == null)
				tzone = (TimeZone)sess.getAttribute(Attributes.PREFERRED_TIME_ZONE);
			final Object otz = TimeZones.setThreadLocal(tzone);
			old = new Object[] {ol, otz};
		}

		SessionsCtrl.setCurrent(sess);
		return old;
	}
	/* Cleans up the inernationalization attributes.
	 *
	 * @param old which must be the value returned by setup.
	 */
	public static final void cleanup(ServletRequest request, Object old) {
		SessionsCtrl.setCurrent(null);

		if (old != null) {
			final Object[] op = (Object[])old;
			TimeZones.setThreadLocal((TimeZone)op[1]);
			Charsets.cleanup(request, op[0]);
		}
	}
}
