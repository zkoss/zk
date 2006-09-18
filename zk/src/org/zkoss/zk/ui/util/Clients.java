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
import com.potix.zk.au.*;

/**
 * Utilities to send {@link AuResponse} to the client.
 *
 * <p>Utilities here are mainly to control how the client (aka., the browser window)
 * behaves. To get the status, you might refer to {@link com.potix.zk.ui.event.ClientInfoEvent}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @see com.potix.zk.ui.event.ClientInfoEvent
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

	/** Prints the current desktop (aka., browser window) to the client the printer.
	 */
	public static void print() {
		addAuResponse(new AuPrint());
	}

	/** Scrolls the current desktop (aka., browser window) by the specified number of pixels.
	 * If the number passed is positive, the desktop is scrolled down.
	 * If negative, it is scrolled up.
	 * @see #scrollTo
	 * @see com.potix.zk.ui.event.ClientInfoEvent
	 */
	public static final void scrollBy(int x, int y) {
		addAuResponse(new AuScrollBy(x, y));
	}
	/** Scrolls the current desktop (aka., browser window) to the specified location (in pixels).
	 *
	 * @see #scrollBy
	 * @see com.potix.zk.ui.event.ClientInfoEvent
	 */
	public static final void scrollTo(int x, int y) {
		addAuResponse(new AuScrollTo(x, y));
	}
	/** Resizes the current desktop (aka., browser window) by the specified number of pixels.
	 * If the numbers passed are positive, the desktop size is increased.
	 * Negative numbers reduce the size of the desktop.
	 *
	 * @see #resizeTo
	 * @see com.potix.zk.ui.event.ClientInfoEvent
	 */
	public static final void resizeBy(int x, int y) {
		addAuResponse(new AuResizeBy(x, y));
	}
	/** Resizes the current desktop (aka., browser window) to the specified size (in pixels).
	 *
	 * @see #resizeBy
	 * @see com.potix.zk.ui.event.ClientInfoEvent
	 */
	public static final void resizeTo(int x, int y) {
		addAuResponse(new AuResizeTo(x, y));
	}
	/** Moves the current desktop (aka., browser window) by the specified number of pixels.
	 * If the number passed is positive, the desktop is moved down.
	 * If negative, it is moved up.
	 * @see #moveTo
	 * @see com.potix.zk.ui.event.ClientInfoEvent
	 */
	public static final void moveBy(int x, int y) {
		addAuResponse(new AuMoveBy(x, y));
	}
	/** Moves the current desktop (aka., browser window) to the specified location (in pixels).
	 *
	 * @see #moveBy
	 * @see com.potix.zk.ui.event.ClientInfoEvent
	 */
	public static final void moveTo(int x, int y) {
		addAuResponse(new AuMoveTo(x, y));
	}

	/** Asks the browser to evaluate the specified JavaScript.
	 * <p>It has no effect if the client doesn't support JavaScript.
	 *
	 * @param javaScript the javaScript codes to run at the browser
	 */
	public static final void evalJavaScript(String javaScript) {
		addAuResponse(new AuScript(null, javaScript));
	}
}
