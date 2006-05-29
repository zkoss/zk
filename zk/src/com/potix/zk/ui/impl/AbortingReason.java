/* AbortingReason.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Apr 30 18:11:01     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import com.potix.zk.au.AuResponse;

/**
 * The aborting reason used with {@link UiVisualizer#setAbortingReason}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/05/29 04:28:05 $
 */
public interface AbortingReason {
	/** Returns whether to abort the execution.
	 */
	public boolean isAborting();
	/** Returns the response representing this aborting reason.
	 * It is called when {@link UiEngineImpl} is about to abort
	 * the current execution due to {@link UiVisualizer#setAbortingReason}.
	 */
	public AuResponse getResponse();
}
