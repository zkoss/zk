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
package org.zkoss.zk.ui.scripting;

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
	/** Returns the class defined in the beanshell.
	 */
	public Class getClass(String clsnm) throws ClassNotFoundException;
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

	/** Copies the variables and methods from the specified
	 * the name space.
	 *
	 * @param filter to filter what variables to copy. Ignored if null.
	 */
	public void copy(Namespace from, Filter filter);
	/** Writes the name and value of the variables of this namespace
	 * to the specified stream.
	 *
	 * <p>If the variable's value is not serializable, it won't be written.
	 *
	 * <p>To read back, use {@link #read}.
	 */
	public void write(java.io.ObjectOutputStream s, Filter filter)
	throws java.io.IOException;
	/** Reads the name and value of the variable from the specified input
	 * stream.
	 * @see #write
	 */
	public void read(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException;

	/** Backup the specfied variable, such that it can be restored with
	 * {@link #restoreVariables}.
	 *
	 * <p>Backup-ed variables are gathered in a block such that all of them
	 * in the same block are restored at once when {@link #restoreVariables}
	 * is called.
	 *
	 * @param name the variable to backup. If null and newBlock is true,
	 * it simply creates a new block. If null and newBlock is false, nothing
	 * happens.
	 * @param newBlock whether to create a new block. If true, a new block
	 * is created and the specified variable and following {@link #backupVariable}
	 * are gathered in this new block.
	 * If false, the variable's value is added to the same block of the previous
	 * invocation of {@link #backupVariable}.
	 */
	public void backupVariable(String name, boolean newBlock);
	/** Restores the variables that are backup-ed by {@link #backupVariable}
	 * in the same block.
	 */
	public void restoreVariables();

	/** The filter used with {@link Namespace#copy} and {@link Namespace#write}.
	 */
	public interface Filter {
		/** Whether to accept the specified variable name and its value.
		 */
		public boolean accept(String name, Object value);
	};
}
