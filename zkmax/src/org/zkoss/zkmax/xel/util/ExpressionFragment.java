/* ExpressionFragment.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  3 10:50:56     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.util;

import java.util.List;
import java.util.LinkedList;

import org.zkoss.lang.Objects;

/** Represents an expression fragment of an expression.
 * When {@link #parse} is called, a list of fragments is returned.
 * Each of them is either a Sring instnce or a {@link ExpressionFragment}
 * instance.
 *
 * <p>For example, "ab${x + y}cd${z}" is broken into the following
 * segments, when {@link #parse} is called:<br/>
 * String("ab"), ExpressionFragment("x+y"), String("cd") and
 * ExpressionFragment("z").
 *
 * <p>It is used to implement {@link org.zkoss.xel.ExpressionFactory}
 * based on an evaluator that doesn't support the syntax of ${expr}.
 * The users of XEL expressions rarely needs to use this class.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class ExpressionFragment implements java.io.Serializable {
	private final String _expr;
	/** Parses an expression into a list of fragments.
	 * Each of them is either a Sring instnce or a {@link ExpressionFragment}
	 * instance.
	 *
	 * <p>For example, "ab${x + y}cd${z}" is broken into the following
	 * segments, when {@link #parse} is called:<br/>
	 * String("ab"), ExpressionFragment("x+y"), String("cd") and
	 * ExpressionFragment("z").
	 *
	 * @param expr the expression that may or may not contain one or
	 * multiple ${expr}. It cannot be null.
	 */
	public static final List parse(String expr) {
		final List frags = new LinkedList();
		for (int j = 0, len = expr.length();;) {
			int k = expr.indexOf("${", j);
			int l = k >= 0 ? expr.indexOf('}', k + 2): 0;
			if (k < 0 || l < 0) {
				if (j < len)
					frags.add(unescape(expr.substring(j)));
				return frags; //done
			}

			if (k > j)
				frags.add(unescape(expr.substring(j, k)));
			if (l > k + 2)
				frags.add(new ExpressionFragment(expr.substring(k + 2, l)));
			j = l + 1;
		}
	}
	/** Converts \$\{ to ${.
	 */
	private static final String unescape(String s) {
		StringBuffer sb = null;
		for (int j = 0, len = s.length();;) {
			int k = s.indexOf("\\$\\{", j);
			if (k < 0) {
				if (j == 0) return s; //nothing to convert
				return sb.append(s.substring(j)).toString();
			}

			if (sb == null) sb = new StringBuffer(len);
			sb.append(s.substring(j, k)).append("${");
			j = k + 4;
		}
	}

	private ExpressionFragment(String expr) {
		_expr = expr;
	}
	/** Returns the expression.
	 */
	public String getExpression() {
		return _expr;
	}

	//Object//
	public int hashCode() {
		return Objects.hashCode(_expr);
	}
	public boolean equals(Object o) {
		return o instanceof ExpressionFragment &&
			Objects.equals(_expr, ((ExpressionFragment)o)._expr);
	}
	public String toString() {
		return _expr;
	}
}
