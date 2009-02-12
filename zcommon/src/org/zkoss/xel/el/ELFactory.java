/* ELFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 17:00:40     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.el;

import org.zkoss.xel.Expression;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;

import org.zkforge.apache.commons.el.ExpressionEvaluatorImpl;

/**
 * An implementation based on ZK Commons EL (zcommons-el.jar: 
 * org.zkforge.apache.commons.el.ExpressionEvaluatorImpl).
 * ZK commons-EL is a peformance enhancement version of Apache Commons EL.
 * It is also independent of JSP 2.0.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ELFactory implements ExpressionFactory {
	private final ExpressionEvaluatorImpl _eval;

	public ELFactory() {
		_eval = new ExpressionEvaluatorImpl();
	}

	//ExpressionFactory//
	public boolean isSupported(int feature) {
		return feature == FEATURE_FUNCTION;
	}
	public Expression parseExpression(XelContext xelc, String expression,
	Class expectedType)
	throws XelException {
		return new ELXelExpression(
			_eval.parseExpression(expression, expectedType,
				xelc != null ? xelc.getFunctionMapper(): null));
	}
	public Object evaluate(XelContext xelc, String expression,
	Class expectedType)
	throws XelException {
		return _eval.evaluate(expression, expectedType,
			xelc != null ? xelc.getVariableResolver(): null,
			xelc != null ? xelc.getFunctionMapper(): null);
	}
}
