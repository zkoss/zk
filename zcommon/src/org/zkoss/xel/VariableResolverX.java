/* VariableResolverX.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 29 13:55:19 TST 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.xel;

/**
 * An extension of {@link VariableResolver} to have more control to
 * resolve the variables.
 * <p>With {@link VariableResolver}, {@link VariableResolver#resolveVariable}
 * is called to resolve a top-level variable (for example, <code>${a.b.c}</code>
 * where <code>a</code> is a top-levelvariable).
 * <p>With {@link VariableResolverX}, {@link #resolveVariable(XelContext, Object, Object)}
 * is called to resolve not only top-level variables but also properties
 * (for example, <code>${a.b.c}</code> where <code>b</code> and <code>c</code>
 * are properties).
 * <p>If {@link VariableResolverX} is implemented, {@link VariableResolver#resolveVariable}
 * is ignored. In other words, EL evaluator always invoke
 * {@link #resolveVariable(XelContext, Object, Object)}.
 * <p>Notice you have to follow the same rule to invoke the <code>resolveVariable</code> method.
 * And there is a method called {@link org.zkoss.xel.util.Evaluators#resolveVariable}
 * that can be used to do the job.
 * @author tomyeh
 * @since 5.0.0
 */
public interface VariableResolverX extends VariableResolver {
	/** Resolves the the given variable on the given base object.
	 *
	 * <p>It resolves not only top-level variables but also properties.
	 * For example, when resloving <code>foo.duke</code>,
	 * <code>resolveVariable(ctx, null, "foo")</code> is called first.
	 * And if it returns an non-null object,
	 * <code>resolveVariable(ctx, foo, "duke"),/code> is called then
	 * (where we assume the returned object in the previous call is <code>foo</code>).
	 *
	 * @param ctx the context of this evaluation
	 * @param base the base object whose property value is to be returned,
	 * or null to reslove a top-level variable.
	 * @param name the name of the variable (or property) to resolve
	 */
	public Object resolveVariable(XelContext ctx, Object base, Object name)
	throws XelException;
}
