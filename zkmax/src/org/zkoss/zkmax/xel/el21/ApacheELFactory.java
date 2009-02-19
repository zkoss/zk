/* ApacheELFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 11:12:14     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.el21;

import javax.el.ELContext;

import org.zkoss.xel.Expression;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;

/**
 * An implementation based on org.apache.el.ExpressionFactoryImpl.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ApacheELFactory implements ExpressionFactory {
	private final javax.el.ExpressionFactory _expf;
	public ApacheELFactory() {
		_expf = newExpressionFactory();
	}

	//ExpressionFactory//
	public boolean isSupported(int feature) {
		return feature == FEATURE_FUNCTION;
	}
	public Expression parseExpression(XelContext xelc, String expression,
	Class expectedType)
	throws XelException {
		return new ELXelExpression(
			_expf.createValueExpression(
				new XelELContext(xelc), expression, expectedType));
	}
	public Object evaluate(XelContext xelc, String expression,
	Class expectedType)
	throws XelException {
		final ELContext ctx = new XelELContext(xelc);
		return _expf.createValueExpression(
				ctx, expression, expectedType).getValue(ctx);
	}

	/** Returns the EL expression factory.
	 * <p>Default: Use org.apache.el.ExpressionFactoryImpl.
	 * <p>You might override it to use a different implementation.
	 */
	protected javax.el.ExpressionFactory newExpressionFactory() {
		return new org.apache.el.ExpressionFactoryImpl();
	}
}
