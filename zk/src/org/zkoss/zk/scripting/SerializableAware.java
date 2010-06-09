/* SerializableAware.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb  7 14:33:36     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

/**
 * An extra interface implemented by an interpreter ({@link Interpreter})
 * if it supports serialization.
 *
 * @author tomyeh
 * @see Interpreter
 */
public interface SerializableAware {
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
	 *
	 * @see #write
	 */
	public void read(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException;

	/** The filter used with {@link SerializableAware#write}.
	 */
	public interface Filter {
		/** Whether to accept the specified variable name and its value.
		 */
		public boolean accept(String name, Object value);
	};
}
