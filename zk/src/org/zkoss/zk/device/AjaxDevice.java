/* AjaxDevice.java

	Purpose:
		
	Description:
		
	History:
		Mon May 14 19:13:07     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.device;

import java.util.Locale;
import java.io.IOException;

import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.http.Wpds;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.MessageLoader;
import org.zkoss.zk.au.out.AuScript;

/**
 * Represents a Web browser with the Ajax support.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public class AjaxDevice extends GenericDevice {
	/** Return false to indicate it is not cacheable.
	 */
	public boolean isCacheable() {
		return false;
	}
	public Boolean isCompatible(String userAgent) {
		userAgent = userAgent.toLowerCase(java.util.Locale.ENGLISH);
		return userAgent.indexOf("mozilla") >= 0 
			|| userAgent.indexOf("msie ") >= 0
			|| userAgent.indexOf("gecko/") >= 0
			|| userAgent.indexOf("safari") >= 0
			|| userAgent.indexOf("opera") >= 0 ? Boolean.TRUE: null;
	}
	/** Returns <code>text/html</code>
	 */
	public String getContentType() {
		return "text/html";
	}
	/** Returns <code>&lt;!DOCTYPE html&gt;</code>. (since 7.0.0)
	 */
	public String getDocType() {
		return "<!DOCTYPE html>";
	}

	/** Converts a package to a path that can be accessible by the class loader
	 * (classpath).
	 * <p>Default: "/js/" + pkg + ".wpd";
	 * @since 5.0.4
	 */
	public String packageToPath(String pkg) {
		return "/js/" + pkg + ".wpd";
	}
	/** Converts a relative path to an absolute path that can be accessible by
	 * the class loader (classpath).
	 * <p>Default: "/js/" + path (if path doesn't start with '/' or '~').
	 * @param path the path (never null).
	 * It is assumed to be a relative path if not starting with '/' or '~'.
	 * @since 5.0.4
	 */
	public String toAbsolutePath(String path) {
		final char cc = path.length() > 0 ? path.charAt(0): (char)0;
		return cc != '/' && cc != '~' ? "/js/" + path: path;
	}

	public void reloadMessages(Locale locale)
	throws IOException {
		if (locale == null)
			locale = Locales.getCurrent();

		final StringBuffer sb = new StringBuffer(4096);
		final Locale oldl = Locales.setThreadLocal(locale);
		try {
			final Execution exec = Executions.getCurrent();
			sb.append(Devices.loadJavaScript(exec, "~./js/zk/lang/msgzk*.js"));
			sb.append(Wpds.outLocaleJavaScript());
			for (LanguageDefinition langdef : LanguageDefinition.getByDeviceType(getType()))
				for (MessageLoader loader : langdef.getMessageLoaders())
					loader.load(sb, exec);
		} finally {
			Locales.setThreadLocal(oldl);
		}
		Clients.response("zk.reload", new AuScript(null, sb.toString()));
	}
}
