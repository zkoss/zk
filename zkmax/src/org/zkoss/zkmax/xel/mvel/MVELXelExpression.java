/* MVELXelExpression.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Sep  2 22:08:01     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.mvel;

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
	/** A list of fragments, either a string or a parsed expression
	 */
	private final Object[] _frags;
	private final Class _expected;
	/*package*/ MVELXelExpression(Object[] frags, Class expectedType) {
		_frags = frags;
		_expected = expectedType;
	}

	//Expression//
	public Object evaluate(XelContext ctx) {
		final XelMVELResolver resolver =
			new XelMVELResolver(ctx.getVariableResolver());

		if (_frags.length == 1) { //optimize this most common case
			return Classes.coerce(_expected,
				_frags[0] instanceof String ? _frags[0]:
				MVEL.executeExpression(_frags[0], resolver));
		}

		final StringBuffer sb = new StringBuffer(256);
		for (int j = 0; j < _frags.length; ++j) {
			if (_frags[j] instanceof String) {
				sb.append(_frags[j]);
			} else {
				Object val = MVEL.executeExpression(_frags[j], resolver);
				if (val != null)
					sb.append(val);
			}	
		}
		return Classes.coerce(_expected, sb.toString());
	}
}
