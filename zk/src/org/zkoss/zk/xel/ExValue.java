/* ExValue.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 10:19:38     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.xel;

import org.zkoss.lang.Classes;
import org.zkoss.xel.Expression;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.XelException;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;

/**
 * A string value that might carries an expression.
 * It is serializable and the expression is parsed by demand.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ExValue implements java.io.Serializable {
	private String _value;
	private Class _expected;
	/** Coerced value. Used only if _expr is DUMMY_EXPRESSION. */
	private transient Object _coercedVal = Objects.UNKNOWN;
	private transient Expression _expr;

	/** Constructor.
	 * @param value the value. It can be null.
	 */
	public ExValue(String value, Class expectedType) {
		if (expectedType == null)
			throw new IllegalArgumentException();
		_value = value;
		_expected = expectedType;
	}

	/** Tests whether it is an expression.
	 * Note: it is a wild guess. In other words, it returns false
	 * only if 100% not an expression.
	 */
	public boolean isExpression() {
		return _expr == null ? _value != null && _value.indexOf("${") >= 0:
			_expr != Expressions.DUMMY_EXPRESSION;
	}

	/** Returns the raw value.
	 * The raw value is the value passed to the constructor.
	 * That is, it might contain EL expressions.
	 */
	public final String getRawValue() {
		return _value;
	}
	/** Sets the raw value.
	 * @param value the value. It can be null.
	 */
	public void setRawValue(String value) {
		if (!Objects.equals(value, _value)) {
			_value = value;
			_expr = null;
			_coercedVal = Objects.UNKNOWN;
		}
	}
	/** Returns the expected type.
	 */
	public final Class getExpectedType() {
		return _expected;
	}
	/** Sets the expected type.
	 */
	public final void setExpectedType(Class expectedType) {
		if (expectedType == null)
			throw new IllegalArgumentException();

		if (_expected != expectedType) {
			_expected = expectedType;
			if (_expr != Expressions.DUMMY_EXPRESSION) _expr = null; //re-parse
			_coercedVal = Objects.UNKNOWN;
		}
	}

	/** Returns the value after evaluation.
	 */
	public Object getValue(Evaluator eval, Page page)
	throws XelException {
		if (_expr == null) init(eval);
		return _expr == Expressions.DUMMY_EXPRESSION ?
			coerce(): eval.evaluate(page, _expr);
	}
	/** Returns the value after evaluation.
	 */
	public Object getValue(Evaluator eval, Component comp)
	throws XelException {
		if (_expr == null) init(eval);
		return _expr == Expressions.DUMMY_EXPRESSION ?
			coerce(): eval.evaluate(comp, _expr);
	}
	private Object coerce() {
		if (_coercedVal == Objects.UNKNOWN)
			_coercedVal = Classes.coerce(_expected, _value);
		return _coercedVal;
	}
	private void init(Evaluator eval)
	throws XelException {
		if (_value != null && _value.indexOf("${") >= 0) {
			_expr = eval.parseExpression(_value, _expected);
		} else {
			_expr = Expressions.DUMMY_EXPRESSION; //to denote not-an-expr
		}
	}

	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		_coercedVal = Objects.UNKNOWN;
	}

	public String toString() {
		return _value;
	}
	public int hashCode() {
		return Objects.hashCode(_value);
	}
	public boolean equals(Object o) {
		if (o instanceof ExValue) {
			final ExValue val = (ExValue)o;
			return Objects.equals(val._value, _value)
				&& Objects.equals(val._expected, _expected);
		}
		return false;
	}
}
