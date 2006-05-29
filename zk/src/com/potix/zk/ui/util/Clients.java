/* Clients.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May 26 14:25:06     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.util;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Executions;
import com.potix.zk.au.AuResponse;
import com.potix.zk.au.AuCloseErrorBox;
import com.potix.zk.au.AuSubmitForm;

/**
 * Utilities to send {@link AuResponse} to the client.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Clients {
	/** Closes the error box at the browser belonging to
	 * the specified component, if any.
	 */
	public static final void closeErrorBox(Component owner) {
		addAuResponse(new AuCloseErrorBox(owner));
	}
	/** Submits the form with the specified ID.
	 */
	public static final void submitForm(String formId) {
		addAuResponse(new AuSubmitForm(formId));
	}
	/** Submits the form with the specified form.
	 * It assumes the form component is a HTML form.
	 */
	public static final void submitForm(Component form) {
		submitForm(form.getUuid());
	}
	private static final void addAuResponse(AuResponse response) {
		Executions.getCurrent()
			.addAuResponse(response.getCommand(), response);
	}
}
