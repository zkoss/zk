/* CustomConstraint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Apr 11 17:59:43     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
 * <p>Since 5.0.0, {@link ClientConstraint} has the higher priority than
 * {@link CustomConstraint}. In other words, {@link CustomConstraint}
 * is ignored if both defined.
 * On the other hand, the client validation object ({@link ClientConstraint#getClientConstraint})
 * can implement the showCustomError method to do the similar job
 * at the client.
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
