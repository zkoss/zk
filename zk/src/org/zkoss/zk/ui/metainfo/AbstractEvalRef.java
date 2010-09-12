/* AbstractEvalRef.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  4 13:03:22     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.xel.Expression;
import org.zkoss.xel.XelException;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * A skeletal implementation for {@link EvaluatorRef}.
 * @author tomyeh
 * @since 3.0.0
 */
abstract /*package*/ class AbstractEvalRef implements EvaluatorRef {
	//EvaluatorRef//
	public PageDefinition getPageDefinition() {
		return null;
	}

	//Evaluator//
	public Expression parseExpression(String expression, Class<?> expectedType)
	throws XelException {
		return getEvaluator().parseExpression(expression, expectedType);
	}
	public Object evaluate(Page page, Expression expression)
	throws XelException {
		return getEvaluator().evaluate(page, expression);
	}
	public Object evaluate(Component comp, Expression expression)
	throws XelException {
		return getEvaluator().evaluate(comp, expression);
	}
}
