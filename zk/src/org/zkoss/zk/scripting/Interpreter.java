/* Interpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 14:51:22     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

/**
 * The interpter used to interpret the zscript codes.
 *
 * <p>Interpreters that don't support {@link Namespace} could derive from
 * {@link NamespacelessInterpreter}.
 *
 * @author tomyeh
 */
public interface Interpreter {
	/** Evaluates the script against the specified namespace.
	 *
	 * @param ns the namespace. Ignored if null.
	 * Note: Unlike {@link org.zkoss.zk.ui.Page#interpret} which assumes
	 * {@link org.zkoss.zk.ui.Page#getNamespace}, this method simply
	 * ignores namespace if null.
	 */
	public void interpret(String script, Namespace ns);

	/** Returns the class defined in this interpreter, or null if not found.
	 *
	 * <p>Note: unlike {@link org.zkoss.zk.ui.Component#getClass}, this method
	 * returns null instead of throwing ClassNotFoundException.
	 */
	public Class getClass(String clsnm);
	/** Returns the method of the specified name defined in this interpreter,
	 * or null if not defined.
	 *
	 * @param argTypes the list of argument (aka., parameter) types.
	 * If null, Class[0] is assumed.
	 */
	public Method getMethod(String name, Class[] argTypes);

	/** Returns the value of a variable defined in this interpreter.
	 *
	 * <p>Note: Unlike {@link Namespace#getVariable}, this method
	 * also retrieve variables defined when executing the script.
	 */
	public Object getVariable(String name);
}
