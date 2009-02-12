/* Interpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 14:51:22     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import org.zkoss.xel.Function;
import org.zkoss.zk.ui.Page;

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

	/** Evaluates the script against the specified namespace.
	 *
	 * <p>Implementation Note:
	 * <ol>
	 * <li>The implementation has to concatenate
	 * the string returned by
	 * {@link org.zkoss.zk.ui.metainfo.LanguageDefinition#getEachTimeScript}
	 * if not null.</li>
	 * <li>The implementation must use {@link Namespaces#getCurrent}
	 * to retrieve the current namesace if the ns argument is null.
	 *
	 * @param ns the namspace. If null, the current namespace is assumed.
	 * The current namespace is {@link Namespaces#getCurrent}, which
	 * is the event target's namespace, if the thread is processing an event.
	 * The event target is {@link org.zkoss.zk.ui.event.Event#getTarget}.
	 * Otherwise, the current namespace is the owner page's namespace
	 * ({@link #getOwner}.
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
	 * @since 3.0.0
	 */
	public Function getFunction(String name, Class[] argTypes);

	/** Tests whether the variable is defined in this interpreter.
	 * Note: it doesn't search the namespace ({@link Namespace}).
	 *
	 * @since 2.4.0
	 */
	public boolean containsVariable(String name);
	/** Returns the value of a variable defined in this interpreter.
	 * Note: it doesn't search the namespace ({@link Namespace}).
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
