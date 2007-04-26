/* HierachicalAware.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Apr 26 16:49:56     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

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
	/** Returns the value of a variable defined in this interpreter.
	 * Note: it doesn't search the namespace.
	 *
	 * <p>It is similar to {@link Interpreter#getVariable(String)}, except
	 * it uses the specified namespace as a reference to identify the
	 * correct scope for searching the variable.
	 *
	 * @param ns the namespace used as a reference to identify the
	 * correct scope for searching the variable.
	 * Note: this method doesn't look for any variable stored in ns.
	 */
	public Object getVariable(Namespace ns, String name);
}
