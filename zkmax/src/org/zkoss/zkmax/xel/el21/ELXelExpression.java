/* ELXelExpression.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 15:27:59     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.el21;

import javax.el.ValueExpression;

import org.zkoss.xel.XelContext;
import org.zkoss.xel.Expression;
import org.zkoss.xel.XelException;

/**
 * A XEL expression that is based on EL expression.
 *
 * @author tomyeh
 * @since 3.0.0
 */
/*package*/ class ELXelExpression implements Expression, java.io.Serializable {
	private final ValueExpression _expr;

	/*package*/ ELXelExpression(ValueExpression expr) {
		_expr = expr;
	}

	public Object evaluate(XelContext xelc)
	throws XelException {
		return _expr.getValue(new XelELContext(xelc));
	}
}
