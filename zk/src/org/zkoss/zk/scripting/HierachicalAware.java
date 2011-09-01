/* HierachicalAware.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 26 16:49:56     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import org.zkoss.xel.Function;
import org.zkoss.zk.ui.ext.Scope;

/**
 * An extra interface implemented by an interpreter ({@link Interpreter})
 * if it supports the hierachical scopes.
 *
 * <p>By supporting the hierachical scopes we mean the interpreter
 * associates one interpreter-dependent scope with each ZK's
 * {@link org.zkoss.zk.ui.IdSpace}. And, variables, classes and methods defined
 * in zscript are then stored in an individual scope depending on
 * the scope when calling {@link Interpreter#interpret}.
 *
 * <p>On the other hand, if the interpreter doesn't support the hierachical
 * scopes, it maintains only one global scope and all variables, classes
 * and functions are defined in the global scope.
 *
 * @author tomyeh
 * @see Interpreter
 */
public interface HierachicalAware {
	/** Tests whether a variable defined in this interpreter.
	 * Note: it doesn't search the scope ({@link Scope}).
	 *
	 * <p>It is similar to {@link Interpreter#containsVariable}, except
	 * it uses the specified scope as a reference to identify the
	 * correct scope for searching the variable.
	 *
	 * @param scope the scope used as a reference to identify the
	 * correct scope for searching the variable.
	 * Note: this method doesn't look for any variable stored in scope.
	 * @since 5.0.0
	 */
	public boolean containsVariable(Scope scope, String name);
	/** Returns the value of a variable defined in this interpreter's
	 * scope identified by the specified scope.
	 * Note: it doesn't search the specified scope ({@link Scope}).
	 *
	 * <p>It is similar to {@link Interpreter#getVariable}, except
	 * it uses the specified scope as a reference to identify the
	 * correct scope for searching the variable.
	 *
	 * @param scope the scope used as a reference to identify the
	 * correct scope for searching the variable.
	 * Note: this method doesn't look for any variable stored in scope.
	 * @since 5.0.0
	 */
	public Object getVariable(Scope scope, String name);
	/** Sets the value of a variable to this interpreter's scope
	 * identified by the specified scope.
	 *
	 * <p>It is similar to {@link Interpreter#setVariable}, except
	 * it uses the specified scope as a reference to identify the
	 * correct scope for storing the variable.
	 *
	 * @param scope the scope used as a reference to identify the
	 * correct scope for searching the variable.
	 * Note: this method doesn't look for any variable stored in scope.
	 * @since 5.0.0
	 */
	public void setVariable(Scope scope, String name, Object value);
	/** Removes the value of a variable defined in the interpreter's
	 * scope identified by the specified scope.
	 *
	 * <p>It is similar to {@link Interpreter#unsetVariable}, except
	 * it uses the specified scope as a reference to identify the
	 * correct scope for removing the variable.
	 *
	 * @param scope the scope used as a reference to identify the
	 * correct scope for searching the variable.
	 * Note: this method doesn't look for any variable stored in scope.
	 * @since 5.0.0
	 */
	public void unsetVariable(Scope scope, String name);

	/** Returns the method of the specified name defined in
	 * this interpreter's scope identified by the specified scope,
	 * or null if not defined.
	 *
	 * <p>It is similar to {@link Interpreter#getFunction}, except
	 * it uses the specified scope as a reference to identify the
	 * correct scope for searching the variable.
	 *
	 * @param scope the scope used as a reference to identify the
	 * correct scope for searching the method.
	 * Note: this method doesn't look for any variable stored in scope.
	 * @param argTypes the list of argument (aka., parameter) types.
	 * If null, Class[0] is assumed.
	 * @since 5.0.0
	 */
	public Function getFunction(Scope scope, String name, Class[] argTypes);
}
