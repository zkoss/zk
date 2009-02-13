/* Action.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  5 20:29:31     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action;

import java.io.IOException;

import org.zkoss.web.servlet.dsp.DspException;

/**
 * An action that could be used with
 * {@link org.zkoss.web.servlet.dsp.InterpreterServlet}.
 * It is like a tag in JSP.
 *
 * @author tomyeh
 */
public interface Action {
	/** Processes the action and renders the output to
	 * {@link ActionContext#getOut}.
	 *
	 * @param nested whether there is any nested content.
	 */
	public void render(ActionContext ac, boolean nested)
	throws DspException, IOException;
}
