/* ParseContext.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 29 19:06:12     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import org.zkoss.xel.XelContext;
import org.zkoss.xel.ExpressionFactory;

/**
 * The context used during parsing.
 *
 * @author tomyeh
 * @since 3.0.0
 */
/*package*/ interface ParseContext extends XelContext {
	/** Returns the expression factory.
	 */
	public ExpressionFactory getExpressionFactory();
}
