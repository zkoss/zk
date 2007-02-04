/* Namespace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 10 15:59:29     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

/**
 * To represent the name space for storing variables and functions.
 * There are two ways to declare variables: by zscirpt, or by
 * {@link org.zkoss.zk.ui.Component#setVariable}/
 * {@link org.zkoss.zk.ui.Page#setVariable}.
 *
 * <p>Each ID space ({@link org.zkoss.zk.ui.IdSpace} has an independent
 * name space to store varaibles and functions.
 * It is a mimic concept of ID space to work with BeanShell (and other
 * interpreter).
 *
 * @author tomyeh
 * @see Interpreter
 */
public interface Namespace {
	/** Returns the class defined in the beanshell, or null if not found.
	 */
	public Class getClass(String clsnm);
	/** Returns the variable of the specified name, or null if not
	 * defined.
	 * @param local whether not to search its ancestor.
	 * If false and the current ID space doen't define the variable,
	 * it searches up its ancestor (via {@link #getParent}) to see
	 * any of them has defined the specified variable.
	 */
	public Object getVariable(String name, boolean local);
	/** Sets the variable of the specified name.
	 * @param local whether not to set the variable to this name space
	 * directly. If false, it searches whether the variable is defined
	 * in any of its ancestor (via {@link #getParent}). If local = false
	 * and the ancestor is found, the value is stored to the ancestor
	 * instead of this.
	 * @exception org.zkoss.zk.ui.UiException if failed.
	 */
	public void setVariable(String name, Object value, boolean local);
	/** Unsets a variable from the current ID space.
	 * <p>Unlike {@link #setVariable}, this method removed only
	 * the variable defined in the ID space cotnaining this component.
	 */
	public void unsetVariable(String name);

	/** Returns the method of the specified name, or null if not defined.
	 *
	 * @param argTypes the list of argument (aka., parameter) types.
	 * If null, Class[0] is assumed.
	 */
	public Method getMethod(String name, Class[] argTypes, boolean local);

	/** Returns the parent name space, or null if this is topmost.
	 */
	public Namespace getParent();
	/** Sets the parent name space.
	 */
	public void setParent(Namespace parent);

	/** Returns the native name space.
	 */
	public Object getNativeNamespace();

	/** Clones this namespace.
	 *
	 * @param id the identifier
	 */
	public Object clone(String id);

	/** Writes the name and value of the variables of this namespace
	 * to the specified stream.
	 *
	 * <p>If the variable's value is not serializable, it won't be written.
	 *
	 * <p>To read back, use {@link #read}.
	 *
	 * <p>Note: it doesn't write the parent ({@link #getParent}).
	 */
	public void write(java.io.ObjectOutputStream s, Filter filter)
	throws java.io.IOException;
	/** Reads the name and value of the variable from the specified input
	 * stream.
	 *
	 * <p>Note: it doesn't read the parent ({@link #getParent}).
	 *
	 * @see #write
	 */
	public void read(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException;

	/** The filter used with {@link Namespace#write}.
	 */
	public interface Filter {
		/** Whether to accept the specified variable name and its value.
		 */
		public boolean accept(String name, Object value);
	};
}
