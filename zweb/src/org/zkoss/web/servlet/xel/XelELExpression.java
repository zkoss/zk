/* XelELExpression.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 14:40:03     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import org.zkoss.xel.Expression;
import org.zkoss.xel.util.SimpleXelContext;

/**
 * An EL expression that is based on XEL expression.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class XelELExpression extends javax.servlet.jsp.el.Expression {
	private final Expression _expr;
	/** Constructor.
	 * @param expr the expression. It cannot be null.
	 */
	public XelELExpression(Expression expr) {
		if (expr == null)
			throw new IllegalArgumentException();
		_expr = expr;
	}

	//Expression//
	public Object evaluate(javax.servlet.jsp.el.VariableResolver resolver) {
		return _expr.evaluate(
			resolver != null ?
				new SimpleXelContext(new ELXelResolver(resolver), null): null);
	}
}
