/* Interpretation.java

	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 12:10:43     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp;

import java.io.IOException;

/**
 * Defines an interpretation of a DSP page.
 * It is a parsed result of a DSP page by use of {@link Interpreter#parse}.
 *
 * @author tomyeh
 */
public interface Interpretation {
	/** Interprets this interpretation of a DSP page, and generates
	 * the result to the output specified in {@link DspContext}.
	 *
	 * @param dc the interpreter context; never null.
	 */
	public void interpret(DspContext dc)
	throws DspException, IOException;
}
