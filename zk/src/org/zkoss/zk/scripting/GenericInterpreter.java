/* GenericInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Feb  7 13:50:54     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

/**
 * A skeletal class for implementing a interpreter ({@link Interpreter}) that
 * doesn't support {@link Namespace}.
 *
 * <p>Deriving class usually overrides {@link #exec} instead of {@link #interpret}.
 *
 * @author tomyeh
 */
abstract public class GenericInterpreter implements Interpreter {
	/** Executes the specified script.
	 * Deriving class shall provide an implementation of this method, rather
	 * than overriding {@link #interpret}.
	 *
	 * <p>It is invoked by {@link #interpret} after 'copying' variables from
	 * the namespace to the interpreter.
	 */
	abstract protected void exec(String script);

	/** Sets a variable to this interpreter directly.
	 */
	abstract protected void setVariable(String name, Object value);
	/** Unsets a variable from this interpreter directly.
	 */
	abstract protected void unsetVariable(String name);

	//Interpreter//
	/** Interprets the script against the specified namespace.
	 * It maintains a stack to copy variables from the name space to
	 * this interpreter -- it assumes this interpreter doesn't support
	 * the concept of namespace.
	 *
	 * <p>Deriving class usually override {@link #exec} instead of
	 * this method.
	 */
	public void interpret(String script, Namespace ns) {
		exec(script);
	}
	/** Returns null since retrieving class is not supported.
	 */
	public Class getClass(String clsnm) {
		return null;
	}
	/** Returns null since retrieving methods is not supported.
	 */
	public Method getMethod(String name, Class[] argTypes) {
		return null;
	}
}
