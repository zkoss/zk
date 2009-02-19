/* HierachicalAware.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Apr 26 16:49:56     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import org.zkoss.xel.Function;

/**
 * An extra interface implemented by an interpreter ({@link Interpreter})
 * if it supports the hierachical scopes.
 *
 * <p>By supporting the hierachical scopes we mean the interpreter
 * associates one interpreter-dependent scope with each ZK's
 * {@link Namespace}. And, variables, classes and methods defined
 * in zscript are then stored in an individual scope depending on
 * the namespace when calling {@link Interpreter#interpret}.
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
	 * Note: it doesn't search the namespace ({@link Namespace}).
	 *
	 * <p>It is similar to {@link Interpreter#containsVariable}, except
	 * it uses the specified namespace as a reference to identify the
	 * correct scope for searching the variable.
	 *
	 * @param ns the namespace used as a reference to identify the
	 * correct scope for searching the variable.
	 * Note: this method doesn't look for any variable stored in ns.
	 * @since 2.4.0
	 */
	public boolean containsVariable(Namespace ns, String name);
	/** Returns the value of a variable defined in this interpreter's
	 * scope identified by the specified namespace.
	 * Note: it doesn't search the specified namespace ({@link Namespace}).
	 *
	 * <p>It is similar to {@link Interpreter#getVariable}, except
	 * it uses the specified namespace as a reference to identify the
	 * correct scope for searching the variable.
	 *
	 * @param ns the namespace used as a reference to identify the
	 * correct scope for searching the variable.
	 * Note: this method doesn't look for any variable stored in ns.
	 */
	public Object getVariable(Namespace ns, String name);
	/** Sets the value of a variable to this interpreter's scope
	 * identified by the specified namespace.
	 *
	 * <p>It is similar to {@link Interpreter#setVariable}, except
	 * it uses the specified namespace as a reference to identify the
	 * correct scope for storing the variable.
	 *
	 * @param ns the namespace used as a reference to identify the
	 * correct scope for searching the variable.
	 * Note: this method doesn't look for any variable stored in ns.
	 * @since 2.4.0
	 */
	public void setVariable(Namespace ns, String name, Object value);
	/** Removes the value of a variable defined in the interpreter's
	 * scope identified by the specified namespace.
	 *
	 * <p>It is similar to {@link Interpreter#unsetVariable}, except
	 * it uses the specified namespace as a reference to identify the
	 * correct scope for removing the variable.
	 *
	 * @param ns the namespace used as a reference to identify the
	 * correct scope for searching the variable.
	 * Note: this method doesn't look for any variable stored in ns.
	 * @since 2.4.0
	 */
	public void unsetVariable(Namespace ns, String name);

	/** Returns the method of the specified name defined in
	 * this interpreter's scope identified by the specified namespace,
	 * or null if not defined.
	 *
	 * <p>It is similar to {@link Interpreter#getFunction}, except
	 * it uses the specified namespace as a reference to identify the
	 * correct scope for searching the variable.
	 *
	 * @param ns the namespace used as a reference to identify the
	 * correct scope for searching the method.
	 * Note: this method doesn't look for any variable stored in ns.
	 * @param argTypes the list of argument (aka., parameter) types.
	 * If null, Class[0] is assumed.
	 * @since 3.0.0
	 */
	public Function getFunction(Namespace ns, String name, Class[] argTypes);
}
