/* AbortingReason.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Apr 30 18:11:01     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.au.AuResponse;

/**
 * The aborting reason used to abort the current execution.
 *
 * @author tomyeh
 */
public interface AbortingReason {
	/** Returns whether to abort the execution.
	 */
	public boolean isAborting();
	/** Returns the response representing this aborting reason.
	 * It is called when {@link UiEngine} to append the response representing
	 * this reason.
	 *
	 * <p>Note: it is always called even if {@link #isAborting} returns false.
	 *
	 * @return the response to generate, or null if no response to generate.
	 */
	public AuResponse getResponse();
}
