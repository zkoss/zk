/* WebApps.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 18 15:24:06     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import org.zkoss.lang.Classes;

/**
 * Utilities related to the whole Web application.
 *
 * @author tomyeh
 * @since 3.0.7
 */
public class WebApps {
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
