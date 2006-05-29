/* Interpretation.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 12:10:43     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp;

import java.io.IOException;

/**
 * Defines an interpretation of a DSP page.
 * It is a parsed result of a DSP page by use of {@link Interpreter#parse}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/29 04:27:38 $
 */
public interface Interpretation {
	/** Interprets this interpretation of a DSP page, and generates
	 * the result to the output specified in {@link DSPContext}.
	 *
	 * @param dc the interpreter context; never null.
	 */
	public void interpret(DSPContext dc)
	throws javax.servlet.ServletException, IOException;
}
