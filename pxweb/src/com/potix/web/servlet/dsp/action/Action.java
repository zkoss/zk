/* Action.java

{{IS_NOTE
	$Id: Action.java,v 1.5 2006/03/09 08:40:17 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Sep  5 20:29:31     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp.action;

import java.io.IOException;
import javax.servlet.ServletException;

/**
 * An action that could be used with
 * {@link com.potix.web.servlet.dsp.InterpreterServlet}.
 * It is like a tag in JSP.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/03/09 08:40:17 $
 */
public interface Action {
	/** Processes the action and renders the output to
	 * {@link ActionContext#getOut}.
	 *
	 * @param nested whether there is any nested content.
	 */
	public void render(ActionContext ac, boolean nested)
	throws ServletException, IOException;
}
