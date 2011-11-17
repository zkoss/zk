/* WebApps.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 18 15:24:06     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import org.zkoss.lang.Classes;

/**
 * Utilities related to the Web application.
 *
 * @author tomyeh
 * @since 3.0.7
 */
public class WebApps {
	/** The application for the whole installation. It assumes ZK libraries
	 * are not shared, i.e., installed under <code>WEB-INF/lib</code>.
	 * @since 5.0.9
	 */
	protected static WebApp _wapp;

	/** Returns this Web application, or null if not available.
	 * <p>Notice that this method is useful only if ZK libraries are
	 * NOT shared by multiple Web application (in other words,
	 * they are installed under <code>WEB-INF/lib</code>).
	 * If you share ZK libraries among multiple applications (such as
	 * installing them under <code>shared/lib</code>), the returned instance could
	 * be any of them.
	 *
	 * <p>If you share ZK libraries among multiple applications, you could
	 * retrieve the current Web application by one of the following
	 * depending your context.
	 * <ol>
	 * <li>In an event listener:<br/>
	 * <code>Sessions.getCurrent(false).getWebApp();</code><br/>
	 * Note, since {@link Sessions#getCurrent(boolean)} returns null if it executes
	 * in a working thread (without {@link Execution}).</li>
	 * <li>In a working thread (not current execution):<br/>
	 * <code>WebManager.getWebManager(servletContext).getWebApp();</code><br/>
	 * As shown above, {@link org.zkoss.zk.ui.http.WebManager#getWebManager}
	 * requires a servlet context.</li></ol>
	 * @since 5.0.9
	 */
	public static WebApp getCurrent() {
		final Session sess = Sessions.getCurrent(false);
		return sess != null ? sess.getWebApp(): _wapp;
	}

	/** Returns whether the specified feature is supported.
	 *
	 * @param feature which feature to check. Supported features:
	 * <dl>
	 * <dt><code>professional</code> or <code>pe</code></dt>
	 * <dd>Whether it is ZK PE, i.e.,
	 * whether zkex.jar (and others) are installed.</dd>
	 * <dt><code>enterprise</code> or <code>ee</code></dt>
	 * <dd>Whether it is ZK EE, i.e.,
	 * whether zkmax.jar (and others) are installed.
	 * Note: ZK EE implies "pe".
	 * In other words, if getFeature("ee") is true,
	 * getFeature("pe") must be true.</dd>
	 * </dl>
	 * @since 3.0.7
	 */
	public static boolean getFeature(String feature) {
		final String f = feature.toLowerCase();
		return "pe".equals(f) || "professional".equals(f) ? _pe:
			"ee".equals(f) || "enterprise".equals(f) ? _ee: false;
	}
	/** Returns the edition, such as EE, PE and CE.
	 * Notice that prior to 5.0.1, the return value is one of
	 * Enterprise, Professional and Standard.
	 * @since 3.6.2
	 */
	public static String getEdition() {
		return _ee ? "EE": _pe ? "PE": "CE";
	}
	/** Features. */
	private static final boolean
		_pe = Classes.existsByThread("org.zkoss.zkex.Version"),
		_ee = Classes.existsByThread("org.zkoss.zkmax.Version");
}
