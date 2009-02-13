/* WebApps.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 18 15:24:06     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
	 * <dt>professional</dt>
	 * <dd>Whether it is the professional edition, i.e.,
	 * whether zkex.jar (and others) are installed.</dd>
	 * <dt>enterprise</dt>
	 * <dd>Whether it is the enterprise edition, i.e.,
	 * whether zkmax.jar (and others) are installed.
	 * Note: the enterperise edition implies "professional".
	 * In other words, if getFeature("enterprise") is true,
	 * getFeature("professional") must be true.</dd>
	 * </dl>
	 * @since 3.0.7
	 */
	public static boolean getFeature(String feature) {
		final String f = feature.toLowerCase();
		return "professional".equals(f) ? _profed:
			"enterprise".equals(f) ? _entped: false;
	}
	/** Features. */
	private static final boolean
		_profed = Classes.existsByThread("org.zkoss.zkex.Version"),
		_entped = Classes.existsByThread("org.zkoss.zkmax.Version");
}
