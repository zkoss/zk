/* Fieldable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Nov 25 20:37:39  2002, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang.reflect;

import org.zkoss.util.ModificationException;

/**
 * Represents an object that is able to handle its fields by name.
 *
 * <p>Calcs detects whether an object implements this method. If so,
 * this interface is used. If not, reflection is used.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Fieldable {
	/** Returns the value of the specified field.
	 */
	public boolean hasField(String name);
	/** Returns the value of the specified field, or null if the field
	 * is not found.
	 */
	public Object getField(String name);
	/** Sets the value of the specified field.
	 */
	public void setField(String name, Object value) throws ModificationException;
	/** Removes the binding set by {@link #setField}.
	 */
	public void removeField(String name) throws ModificationException;
}
