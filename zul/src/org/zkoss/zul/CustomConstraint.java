/* CustomConstraint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Apr 11 17:59:43     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;

/**
 * Additional interface implemented with {@link Constraint} to denote
 * a constraint supports a custom way to display the error message.
 * If this interface is implemented, the default error box won't be
 * displayed. Rather, {@link #showCustomError} is called.
 *
 * <p>Note: if this interface is implemented,
 * {@link ClientConstraint} is ignored, since all validation will be done
 * at the server.
 *
 * @author tomyeh
 * @see Constraint
 * @see ClientConstraint
 */
public interface CustomConstraint {
	/** To display the error message in a custom way.
	 * Note: this method is called either with a error or not, depending
	 * on whether ex is null.
	 *
	 * @param comp the component causing the error.
	 * @param ex the error to display, or null to clear
	 * the error message.
	 */
	public void showCustomError(Component comp, WrongValueException ex);
}
