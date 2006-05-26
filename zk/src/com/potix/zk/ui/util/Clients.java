/* Clients.java

{{IS_NOTE
	$Id: Clients.java,v 1.1 2006/05/26 06:41:15 tomyeh Exp $
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
import com.potix.zk.au.AuCloseErrorBox;
import com.potix.zk.au.AuSubmitForm;

/**
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.1 $ $Date: 2006/05/26 06:41:15 $
 */
public class Clients {
	/** Closes the error box at the browser belonging to
	 * the specified component, if any.
	 */
	public static final void closeErrorBox(Component owner) {
		Executions.getCurrent()
			.addAuResponse("closeErrbox", new AuCloseErrorBox(owner));
	}
	/** Submits the form with the specified ID.
	 */
	public static final void submitForm(String formId) {
		Executions.getCurrent()
			.addAuResponse("submitForm", new AuSubmitForm(formId));
	}
	/** Submits the form with the specified form.
	 * It assumes the form component is a HTML form.
	 */
	public static final void submitForm(Component form) {
		submitForm(form.getUuid());
	}
}
