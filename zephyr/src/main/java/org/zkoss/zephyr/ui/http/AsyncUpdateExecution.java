/* AsyncUpdateExecution.java

	Purpose:
		
	Description:
		
	History:
		10:55 AM 2021/11/23, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.http.ExecutionImpl;

/**
 * Represents the execution is in an async update.
 * @author jumperchen
 */
/*package*/ class AsyncUpdateExecution extends ExecutionImpl {
	boolean isRecovering = false;
	/**
	 * Constructs an execution for the given HTTP request.
	 *
	 * @param ctx
	 * @param request
	 * @param response
	 * @param desktop
	 * @param creating which page is being creating for this execution, or
	 */
	public AsyncUpdateExecution(ServletContext ctx, HttpServletRequest request,
			HttpServletResponse response, Desktop desktop, Page creating) {
		super(ctx, request, response, desktop, creating);
	}

	public boolean isAsyncUpdate(Page page) {
		return true;// always true
	}

	public boolean isRecovering() {
		return isRecovering || super.isRecovering();
	}

	public void enableRecovering() {
		isRecovering = true;
	}

	public void disableRecovering() {
		isRecovering = true;
	}
}
