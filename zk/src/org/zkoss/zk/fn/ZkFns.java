/* ZkFns.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun  7 11:09:48     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.fn;

import org.zkoss.zk.ui.Executions;

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
}
