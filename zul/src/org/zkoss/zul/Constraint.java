/* Constraint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:42:37     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;

/**
 * A constraint. It could be anything, but you could use {@link SimpleConstraint}
 * to simplify the task.
 *
 * @author tomyeh
 */
public interface Constraint {
	/** Verifies whether the value is acceptable.
	 * @param comp the component being validated
	 */
	public void validate(Component comp, Object value)
	throws WrongValueException;
	/** Returns the function name in JavaScript used to validate
	 * the value in the client, or null if no client verification is
	 * supported.
	 *
	 * <p>Example: if "zkVld.noEmpty" is returned, then
	 * zkVld.noEmpty(id) is then called.
	 */
	public String getValidationScript();
	/** Returns whether the client's validation is complete.
	 * If true, onChange won't be sent immediately (unless onChange is listened).
	 * If false, onChange is always sent no matter {@link #getValidationScript}
	 * return null or not.
	 */
	public boolean isClientComplete();
	/** Returns the error message, or null if not specified (and default
	 * message shall be used).
	 */
	public String getErrorMessage();
}
