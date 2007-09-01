/* Expressions.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 11:09:33     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;

/**
 * Utilities to use XEL.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class Expressions {
	private static final Log log = Log.lookup(Expressions.class);

	/** An empty function mapper, i.e., it has no function defined at all.
	 * It is serializable.
	 */
	public static final FunctionMapper EMPTY_MAPPER = new EmptyMapper();
	/** An dummy expression that does nothing.
	 * It is usually used as a flag to denote exceptional cases.
	 * It it serializable.
	 */
	public static final Expression DUMMY_EXPRESSION = new DummyExpr();

	/** Instantiates an instance of {@link ExpressionFactory}.
	 *
	 * <p>The default class is {@link org.zkoss.xel.el21.ELFactory}
	 * or {@link org.zkoss.xel.el.ELFactory} depending on
	 * the Web server.
	 * To override it, you can specify the class name in the system
	 * property called "org.zkoss.xel.ExpressionFactory.class".
	 *
	 * <p>For the ZK user, you can override it with zk.xml.
	 *
	 * @exception XelException if the specified class failed to load,
	 * or instantiate.
	 */
	public static final ExpressionFactory newExpressionFactory()
	throws XelException {
		String clsnm =  null;
		try {
			clsnm =	System.getProperty("org.zkoss.xel.ExpressionFactory.class", null);
		} catch (Throwable ex) { //permission might not be allowed
		}

		if (clsnm != null && clsnm.length() > 0) {
			try {
				return (ExpressionFactory)Classes.newInstanceByThread(clsnm);
			} catch (Throwable ex) {
				throw XelException.Aide.wrap(ex, "Unable to instantiate "+clsnm);
			}
		}
		return newDefautFactory();
	}
	/** Instantiates an instance of {@link ExpressionFactory}.
	 *
	 * <p>The default class is {@link org.zkoss.xel.el21.ELFactory}
	 * or {@link org.zkoss.xel.el.ELFactory} depending on
	 * the Web server.
	 * To override it, you can specify the class name in the system
	 * property called "org.zkoss.xel.ExpressionFactory.class".
	 *
	 * <p>For the ZK user, you can override it with zk.xml.
	 *
	 * @param expfcls the class that implements {@link ExpressionFactory},
	 * or null to use the default.
	 * @exception XelException if the specified class failed to load,
	 * or instantiate.
	 */
	public static final ExpressionFactory newExpressionFactory(Class expfcls) {
		if (expfcls != null) {
			try {
				return (ExpressionFactory)expfcls.newInstance();
			} catch (Throwable ex) {
				throw XelException.Aide.wrap(ex, "Unable to instantiate "+expfcls);
			}
		}
		return newDefautFactory();
	}
	private static final ExpressionFactory newDefautFactory() {
		if (_jsp21)
			return new org.zkoss.xel.el21.ELFactory();
		return new org.zkoss.xel.el.ELFactory();
	}
	/** Whether to use JSP2.1. */
	private static final boolean _jsp21;
	static {
		boolean jsp21;
		try {
			Classes.forNameByThread("javax.el.Expression");
			jsp21 = true;
		} catch (Throwable ex) {
			jsp21 = false;
		}
		_jsp21 = jsp21;
		log.info("JSP "+(_jsp21 ? "2.1 or above": "2.0")+" found");
	}

	/** Evaluates an expression.
	 *
	 * @param ctx the context information to evaluate the expression
     * It can be null, in which case no functions are supported for this
     * invocation.
	 * @param expression the expression to be evaluated.
	 * Note: the expression is enclosed
	 * with ${ and }, regardingless what implemetnation is used.
	 * @param expectedType the expected type of the result of the evaluation
	 */
	public static final Object evaluate(XelContext ctx,
	String expression, Class expectedType)
	throws XelException {
		return newExpressionFactory().evaluate(ctx, expression, expectedType);
	}
}
/*package*/ class EmptyMapper
implements FunctionMapper, java.io.Serializable {
	//-- FunctionMapper --//
	public Function resolveFunction(String prefix, String name) {
		return null;
	}
}
/*package*/ class DummyExpr implements Expression, java.io.Serializable {
	public Object evaluate(XelContext ctx) throws XelException {
		return null;
	}
};
