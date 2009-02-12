/* Operation.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 29, 2007 9:20:35 AM     2007, Created by Dennis.Chen
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
 * This interface is for model sharer developer only, you rarely need to use this interface.<br/>
 * A model sharer will add Operation to {@link OperationQueue}, then {@link OperationThread} which monitor this queue
 * will consume operations and {@link #execute(Desktop)} it.<br/>
 * The method {@link #failToExecute(Desktop)} will be invoked when   
 * <ol>
 * <li>Any Exception occurs when execute</li> 
 * <li>Thread is terminate by {@link OperationThread#terminate()}</li>
 * <li>Desktop is no longer available</li>
 * </ol> 
 * 
 * 
 * @author Dennis.Chen
 * @since 3.0.0
 */
public interface Operation {

	/**
	 * Execute the operation.<br/> 
	 * The {@link OperationThread} will activate desktop first,
	 * then call this method, and then call deactivate.<br/>  
	 * @param desktop the desktop which {@link OperationThread} associate to.
	 */
	public void execute(Desktop desktop);
	
	/**
	 * Notify when 
	 * 1.any Exception occurs when execute 
	 * 2.thread is terminate by {@link OperationThread#terminate()}
	 * 3.desktop is not longer available 
	 * @param desktop the Desktop which {@link OperationThread} associate to.
	 */
	public void failToExecute(Desktop desktop);
}
