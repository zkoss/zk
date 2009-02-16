/* ClientConstraint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Apr 11 18:11:54     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
	/** Returns the function name in JavaScript or a Javascript code snippet
	 * used to validate the value at the client, or null if no client
	 * verification is supported.
	 *
	 * <p>There are two formats of the return value:
	 *
	 * <p>Format 1:<br/>
	 * Syntax: <i>function_name</i><br/>
	 * Example: "zkVld.noEmpty"<br/>
	 * What Really Happens:<br/>
	 * <code>zkVld.noEmpty('id')</code> is called at the client side
	 * to validate the input, where id is the component's identifier.
	 *
	 * <p>Format 2:<br/>
	 * Syntax: <i>function_name(arg1, arg2, arg3)</i><br/>
	 * where arg could be #{<i>EL_expression</i>}<br/>
	 * Example: "myValid(#{self},#{when},'more')"<br/>
	 * What Really Happens:<br/>
	 * <code>myValid($e('id'),new Date('2007/06/03'),'more')</code>
	 * is called at the client side
	 * to validate the input, where id is the component's identifier.
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
