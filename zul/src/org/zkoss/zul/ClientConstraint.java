/* ClientConstraint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Apr 11 18:11:54     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;

/**
 * Addition interface implemented with {@link Constraint} to denote
 * how to validate at the client.
 *
 * <p>Note: this interface is ignored if {@link CustomConstraint}
 * is also implemented, since {@link CustomConstraint} causes
 * all validations are processed at the server.
 *
 * @author tomyeh
 * @see Constraint
 * @see CustomConstraint
 */
public interface ClientConstraint {
	/** Returns the function name in JavaScript used to validate
	 * the value at the client, or null if no client verification is
	 * supported.
	 *
	 * <p>Example: if "zkVld.noEmpty" is returned, then
	 * zkVld.noEmpty(id) is then called.
	 */
	public String getClientValidation();
	/** Returns whether the client's validation is complete.
	 * If true, onChange won't be sent immediately (unless onChange is listened).
	 * If false, onChange is always sent no matter {@link #getClientValidation}
	 * return null or not.
	 */
	public boolean isClientComplete();
	/** Returns the error message when the client detects an error,
	 * or null if not specified.
	 *
	 * <p>It is used only if you want to override the default error message
	 * shown by the client. It won't affect the message caused by an exception
	 * thrown by {@link Constraint#validate}.
	 */
	public String getErrorMessage(Component comp);
}
