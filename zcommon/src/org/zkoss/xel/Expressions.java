/* Expressions.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 11:09:33     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

import java.util.Collection;
import java.util.Collections;

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
	/** An empty variable resolver, i.e., it has no variable defined at all.
	 * It is serializable.
	 */
	public static final VariableResolver EMPTY_RESOLVER = new EmptyResolver();

	/** An dummy expression that does nothing.
	 * It is usually used as a flag to denote exceptional cases.
	 * It it serializable.
	 */
	public static final Expression DUMMY_EXPRESSION = new DummyExpr();

	/** The implemetation of {@link ExpressionFactory}. */
	private static Class _expfcls;

	/** Instantiates an instance of {@link ExpressionFactory}.
	 *
	 * <p>The default class is {@link org.zkoss.xel.el.ELFactory}.
	 * To override it, you can specify the class by calling
	 * {@link #setExpressionFactoryClass}.
	 *
	 * <p>For the ZK user, you can override it with zk.xml
	 * or org.zkoss.zk.ui.util.Configuration.
	 *
	 * @exception XelException if the specified class failed to load,
	 * or instantiate.
	 */
	public static final ExpressionFactory newExpressionFactory()
	throws XelException {
		return newExpressionFactory(_expfcls);
	}
	/** Instantiates an instance of {@link ExpressionFactory}.
	 *
	 * <p>The default class is {@link org.zkoss.xel.el.ELFactory}.
	 * To override it, you can specify the class by calling
	 * {@link #setExpressionFactoryClass}.
	 *
	 * <p>For the ZK user, you can override it with zk.xml
	 * or org.zkoss.zk.ui.util.Configuration.
	 *
	 * @param expfcls the class that implements {@link ExpressionFactory},
	 * or null to use the default.
	 * @exception XelException if the specified class failed to load,
	 * or instantiate.
	 */
	public static final ExpressionFactory newExpressionFactory(Class expfcls) {
		if (expfcls == null)
			expfcls = _expfcls;
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
		return new org.zkoss.xel.el.ELFactory();
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

	/** Sets the implementation of the expression factory that shall
	 * be used by the whole system, or null to use the system default.
	 *
	 * <p>Default: null - it means {@link org.zkoss.xel.el.ELFactory}.
	 *
	 * <p>Note: you can only specify an implementation that is compatible
	 * with JSP EL here, since all builtin pages depend on it.
	 *
	 * @param expfcls the implemtation class, or null to use the default.
	 * Note: expfcls must implement {@link ExpressionFactory}.
	 * If null, the system default is used.
	 */
	public static final void setExpressionFactoryClass(Class expfcls) {
		if (expfcls != null && !ExpressionFactory.class.isAssignableFrom(expfcls))
			throw new IllegalArgumentException(expfcls+" must implement "+ExpressionFactory.class);
		_expfcls = expfcls;
	}
	/** Returns the implementation of the expression factory that
	 * is used by the whole system, or null to use the system default.
	 *
	 * @see #setExpressionFactoryClass
	 */
	public static final Class getExpressionFactoryClass() {
		return _expfcls;
	}
}
/*package*/ class EmptyMapper
implements FunctionMapper, java.io.Serializable {
	//-- FunctionMapper --//
	public Function resolveFunction(String prefix, String name) {
		return null;
	}
	public Collection<String> getClassNames() {
		return Collections.emptyList();
	}
	public Class<?> resolveClass(String name) {
		return null;
	}
}
/*package*/ class EmptyResolver
implements VariableResolver, java.io.Serializable {
	public Object resolveVariable(String name) {
		return null;
	}
}
/*package*/ class DummyExpr implements Expression, java.io.Serializable {
	public Object evaluate(XelContext ctx) throws XelException {
		return null;
	}
};
