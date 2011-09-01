/* ELFactory.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 17:00:40     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.el;

import org.zkoss.lang.Classes;
import org.zkoss.xel.Expression;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.FunctionMapper;
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
	/** Used to denote the version of zcommons-el. */
	private static final boolean _v102, _v103;

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
		if (_v103)
			return new ELXelExpression(
				_eval.parseExpression(expression, expectedType));
		if (_v102)
			return new ELXelExpression102(
				_eval.parseExpression(expression, expectedType));

		FunctionMapper mapper = xelc != null ? xelc.getFunctionMapper(): null;
		return new ELXelExpression100(
			_eval.parseExpression(expression, expectedType, mapper),
			expression, mapper, expectedType);
	}
	public Object evaluate(XelContext xelc, String expression,
	Class expectedType)
	throws XelException {
		return _eval.evaluate(expression, expectedType,
			xelc != null ? xelc.getVariableResolver(): null,
			xelc != null ? xelc.getFunctionMapper(): null);
	}

	static {
		boolean v103 = false;
		try {
			Classes.forNameByThread("org.zkforge.apache.commons.el.Resolvers");
			v103 = true;
		} catch (Throwable e) {
		}
		if ((_v103 = v103) == true) {
			_v102 = true;
		} else {
			boolean v102 = false;
			try {
				Classes.forNameByThread("org.zkforge.apache.commons.el.ELExpression");
				v102 = true;
			} catch (Throwable e) {
			}
			_v102 = v102;
		}
	}
}
