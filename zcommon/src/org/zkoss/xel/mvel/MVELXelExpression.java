/* MVELXelExpression.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Sep  2 22:08:01     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.mvel;

import java.io.Serializable;

import org.mvel.MVEL;

import org.zkoss.lang.Classes;
import org.zkoss.xel.Expression;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelContext;

/**
 * A XEL expression that is implemented based on MVEL's expression.
 *
 * @author tomyeh
 * @since 3.0.0
 */
/*package*/ class MVELXelExpression implements Expression, Serializable {
	private final Serializable _expr;
	private final Class _expected;
	/*package*/ MVELXelExpression(Serializable expr, Class expectedType) {
		_expr = expr;
		_expected = expectedType;
	}

	//Expression//
	public Object evaluate(XelContext ctx) {
		final VariableResolver resolver = ctx.getVariableResolver();
		return Classes.coerce(_expected,
			MVEL.executeExpression(_expr, new XelMVELResolver(resolver)));
	}
}
