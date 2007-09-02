/* MVELFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Sep  2 21:04:06     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.mvel;

import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Expression;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;

/**
 * An implementation based on MVEL.
 *
 * <p>Note: MVEL is not completely compatible with JSP EL.
 * For example, it doesn't support {@link org.zkoss.xel.FunctionMapper#resolveFunction}.
 * Rather, it supports {@link org.zkoss.xel.FunctionMapper#getImportedClasses}.
 *
 * <p>See also <a href="http://mvel.codehaus.org/">MVEL website</a>.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class MVELFactory implements ExpressionFactory {
	//ExpressionFactory//
	public boolean isSupported(int feature) {
		return feature == FEATURE_IMPORT;
	}
	public Expression parseExpression(XelContext ctx, String expression,
	Class expectedType)
	throws XelException {
		return null;
	}
	public Object evaluate(XelContext ctx, String expression,
	Class expectedType)
	throws XelException {
		return null;
	}
}
