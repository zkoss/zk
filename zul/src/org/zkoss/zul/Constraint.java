/* Constraint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:42:37     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;

/**
 * A constraint.
 * Instead of implementing this interface from scratch,
 * you may use {@link SimpleConstraint} if applicable.
 *
 * <p>To have better responsiveness, you can handle more or all validations
 * at the client by implementing an additional interface,
 * {@link ClientConstraint}.
 *
 * <p>If you prefer to have a custom way to display
 * the error message (other than the default error box).
 * You can also implement {@link CustomConstraint}.
 * Then, {@link CustomConstraint#showCustomError} is called instead of
 * showing the default error box.
 * In addition, {@link ClientConstraint} is ignored in this case,
 * since all validation will be done at the server.
 *
 * @author tomyeh
 * @see CustomConstraint
 * @see ClientConstraint
 */
public interface Constraint {
	/** Verifies whether the value is acceptable.
	 * @param comp the component being validated
	 */
	public void validate(Component comp, Object value)
	throws WrongValueException;
}
