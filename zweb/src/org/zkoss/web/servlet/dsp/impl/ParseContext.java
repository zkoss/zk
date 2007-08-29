/* ParseContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 29 19:06:12     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.ExpressionEvaluator;

/**
 * The context used during parsing.
 *
 * @author tomyeh
 * @since 3.0.0
 */
/*package*/ interface ParseContext {
	/** Returns the evaluator
	 */
	public ExpressionEvaluator getExpressionEvaluator();
	/** Returns the function mapper.
	 */
	public FunctionMapper getFunctionMapper();
}
