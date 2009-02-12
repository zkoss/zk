/* OperationQueueListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 2, 2007 6:43:38 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkex.zul.impl;

import org.zkoss.zk.ui.Desktop;

/**
 * This class is for model sharer developer only, you rarely need to use this class.<br/>
 * @author Dennis.Chen
 * @since 3.0.0
 */
public interface OperationQueueListener {

	/**
	 * Notify the {@link OperationQueue} no longer available, 
	 * it is usual invoked before the end of {@link OperationThread#run()}   
	 * @param desktop the associated desktop
	 */
	public void queueUnavailable(Desktop desktop);
}
