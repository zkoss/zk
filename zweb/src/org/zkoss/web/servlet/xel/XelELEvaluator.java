/* XelELEvaluator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 14:32:22     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.util.SimpleXelContext;

/**
 * An EL expression evaluator that is based on a XEL expression factory.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class XelELEvaluator
extends javax.servlet.jsp.el.ExpressionEvaluator {
	final ExpressionFactory _expf;

	/** Constructor.
	 *
	 * @param expf the expression factory. It cannot be null.
	 * @exception IllegalArgumentException if expf is null.
	 */
	public XelELEvaluator(ExpressionFactory expf) {
		if (expf == null)
			throw new IllegalArgumentException();
		_expf = expf;
	}

	public javax.servlet.jsp.el.Expression
	parseExpression(String expression, Class expectedType,
	javax.servlet.jsp.el.FunctionMapper mapper) {
		return new XelELExpression(
			_expf.parseExpression(
			mapper != null ?
				new SimpleXelContext(null, new ELXelMapper(mapper)): null,
			expression, expectedType));
	}
	public Object evaluate(String expression, Class expectedType,
	javax.servlet.jsp.el.VariableResolver resolver,
	javax.servlet.jsp.el.FunctionMapper mapper) {
		return _expf.evaluate(
			new SimpleXelContext(
				resolver != null ? new ELXelResolver(resolver): null,
				mapper != null ? new ELXelMapper(mapper): null),
			expression, expectedType);
	}
}
