/* ELFactory.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 20 09:21:11     2011, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.xel.zel;

import org.zkoss.xel.Expression;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.zel.ELContext;

/**
 * An XEL ExpressionFactory implementation based on ZEL ExpressionFactory.
 *
 * @author henrichen
 * @since 6.0.0
 */
public class ELFactory implements ExpressionFactory {
	protected final org.zkoss.zel.ExpressionFactory _expf;
	public ELFactory() {
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
				newELContext(xelc), expression, expectedType));
	}
	public Object evaluate(XelContext xelc, String expression,
	Class expectedType)
	throws XelException {
		final ELContext ctx = newELContext(xelc);
		return _expf.createValueExpression(
				ctx, expression, expectedType).getValue(ctx);
	}
	protected ELContext newELContext(XelContext xelc) {
		return new XelELContext(xelc);
	}
	/** Returns the EL expression factory.
	 * <p>Default: Use org.zkoss.zel.impl.ExpressionFactoryImpl.
	 * <p>You might override it to use a different implementation.
	 */
	protected org.zkoss.zel.ExpressionFactory newExpressionFactory() {
		return new org.zkoss.zel.impl.ExpressionFactoryImpl();
	}
}
