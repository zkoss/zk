/* Interpreter.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 14:51:22     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import org.zkoss.xel.Function;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ext.Scope;

/**
 * Represents an interpter that can interpret the scripting codes.
 *
 * <p>It is easier to implement by extending
 * from {@link org.zkoss.zk.scripting.util.GenericInterpreter}.
 *
 * @author tomyeh
 */
public interface Interpreter {
	/** Initializes the interpreter.
	 * It is called once when the new instance of interpreter is constructed.
	 *
	 * @param zslang the language this interpreter is associated with
	 */
	public void init(Page owner, String zslang);
	/** Called when the interpreter is about to be destroyed.
	 * After called, this interpreter cannot be used again.
	 */
	public void destroy();

	/** Returns the owner of this interpreter.
	 */
	public Page getOwner();
	/** Returns the scripting language this interpreter is associated with.
	 */
	public String getLanguage();

	/** Returns the native interpreter, or null if not available.
	 * The native interpreter depends on the implementation of an interpreter.
	 *
	 * @since 3.0.2
	 */
	public Object getNativeInterpreter();

	/** @deprecated As of release 5.0.0, replaced with {@link #interpret(String, Scope)}
	 * <p>Evaluates the script against the specified namespace.
	 */
	public void interpret(String script, Namespace ns);
	/** Evaluates the script against the specified scope.
	 *
	 * <p>Implementation Note:
	 * <ol>
	 * <li>The implementation has to concatenate
	 * the string returned by
	 * {@link org.zkoss.zk.ui.metainfo.LanguageDefinition#getEachTimeScript}
	 * if not null.</li>
	 * <li>The implementation must use {@link org.zkoss.zk.ui.ext.Scopes#getCurrent}
	 * to retrieve the current namesace if the comp argument is null.
	 *
	 * @param scope the scope as the context to interpret the script.
	 * If null, the current scope is assumed.
	 * The current scope is {@link org.zkoss.zk.ui.ext.Scopes#getCurrent}, which
	 * is the event target's scope, if the thread is processing an event.
	 * The event target is {@link org.zkoss.zk.ui.event.Event#getTarget}.
	 * Otherwise, the current scope is the owner page ({@link #getOwner}.
	 * @since 5.0.0
	 */
	public void interpret(String script, Scope scope);

	/** Returns the class defined in this interpreter, or null if not found.
	 */
	public Class getClass(String clsnm);
	/** Returns the method of the specified name defined in this interpreter,
	 * or null if not defined.
	 *
	 * @param argTypes the list of argument (aka., parameter) types.
	 * If null, Class[0] is assumed.
	 * @since 3.0.0
	 */
	public Function getFunction(String name, Class[] argTypes);

	/** Tests whether the variable is defined in this interpreter.
	 * Note: it doesn't search the attributes ({@link Scope}).
	 *
	 * @since 2.4.0
	 */
	public boolean containsVariable(String name);
	/** Returns the value of a variable defined in this interpreter.
	 * Note: it doesn't search the scope ({@link Scope}).
	 */
	public Object getVariable(String name);
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
