/* ErrorHandling.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec  16 19:29:35     2008, Created by RyanWu
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zkdemo.test;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.GenericRichlet;

/**
 * Used to test richlet.
 * 
 * @author tomyeh
 */
public class ErrorHandling extends GenericRichlet {
	public void service(Page page) {
		Executions.createComponents(page.getDesktop().getWebApp()
				.getConfiguration().getErrorPage(
						page.getDesktop().getDeviceType(),
						(Throwable) Executions.getCurrent().getAttribute(
								"javax.servlet.error.exception")), null, null);
	}
}
