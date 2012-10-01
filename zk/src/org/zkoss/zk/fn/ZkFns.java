/* ZkFns.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun  7 11:09:48     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.fn;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApps;

/**
 * Utilities for using XEL in ZUL.
 *
 * <p>For JSP pages, use {@link JspFns} instead.<br/>
 * For DSP pages, use {@link DspFns} instead.
 *
 * @author tomyeh
 */
public class ZkFns {
	protected ZkFns() {}

	/** Converts the specified URI to absolute if necessary.
	 * Refer to {@link org.zkoss.zk.ui.Execution#toAbsoluteURI}.
	 *
	 * @param skipInclude whether not to convert to an absolute URI if
	 * the current page is included by another page.
	 * When use the include directive, skipInclude shall be true.
	 */
	public static String toAbsoluteURI(String uri, boolean skipInclude) {
		return Executions.getCurrent().toAbsoluteURI(uri, skipInclude);
	}
	/** Converts the specified URI to absolute if not included by another page.
	 * It is a shortcut of {@link #toAbsoluteURI(String, boolean)} with skipInclude
	 * is true.
	 */
	public static String toAbsoluteURI(String uri) {
		return toAbsoluteURI(uri, true);
	//we preserve this method for backward compatibility (since some developers
	//might have old version core.dsp.tld
	}

	/** Returns the build identifier, such as 2007121316.
	 *
	 * <p>Each time ZK is built, a different build identifier is assigned.
	 * @since 6.0.3
	 */
	public static String getBuild() {
		return WebApps.getCurrent().getBuild();
	}

	/** Returns the ZK version, such as "1.1.0" and "2.0.0".
	 * @since 6.0.3
	 */
	public static String getVersion() {
		return WebApps.getCurrent().getVersion();
	}

	/** Returns the edition, such as EE, PE and CE.
	 * Notice that prior to 5.0.1, the return value is one of
	 * Enterprise, Professional and Standard.
	 * @since 6.0.3
	 */
	public static String getEdition() {
		return WebApps.getEdition();
	}
}
