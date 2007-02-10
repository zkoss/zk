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

import org.zkoss.zk.ui.Page;

/**
 * The interpter used to interpret the zscript codes.
 *
 * <p>Interpreters that support {@link Namespace} could derive from
 * {@link org.zkoss.zk.scripting.util.GenericInterpreter}.
 * Interpreters that don't support {@link Namespace} could derive from
 * {@link org.zkoss.zk.scripting.util.NamespacelessInterpreter}.
 *
 * @author tomyeh
 */
public interface Interpreter {
	/** Initializes the interpreter.
	 * It is called once when the interpreter is contructed.
	 *
	 * @param zslang the language this interpreter is associated with
	 */
	public void init(Page owner, String zslang);
	/** Called when the interpreter is about to be detroyed.
	 * After called, this interpreter cannot be used again.
	 */
	public void destroy();

	/** Evaluates the script against the specified namespace.
	 *
	 * <p>Implementation Note: the implementation has to concatenate
	 * the string returned by {@link LanguageDefinition#getEachTimeScript}
	 * if not null.
	 *
	 * @param ns the namespace. Ignored if null.
	 * Note: Unlike {@link org.zkoss.zk.ui.Page#interpret} which assumes
	 * {@link org.zkoss.zk.ui.Page#getNamespace}, this method simply
	 * ignores namespace if null.
	 */
	public void interpret(String script, Namespace ns);

	/** Returns the class defined in this interpreter, or null if not found.
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
	 * @param ignoreNamespace whether to ignore the active namespace, if any.
	 * Note: when {@link #interpret} is called, the namespace specified
	 * in the ns argument becomes the active namespace.
	 * 
	 */
	public Object getVariable(String name, boolean ignoreNamespace);
	/** Sets the value of a variable to this interpreter, as if
	 * they are defined in the interpreter.
	 *
	 * <p>Note: it is not part of any namespace and it has higher prioerty
	 * if its name conflicts with any variable defined in the namespaces.
	 */
	public void setVariable(String name, Object value);
	/** Removes the value of a variable defined in this interpreter.
	 */
	public void unsetVariable(String name);
}
