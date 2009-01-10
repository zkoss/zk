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
	/** Returns the JavaScript snippet that will be evaluated at client
	 * to return a validator, or null if no client constraint is supported.
	 * The validator is later used to validate an input.
	 *
	 * <p>For example,
	 * <pre><code>String getClientConstraint() {
	 *  return "new foo.MyValidator()";
	 *}</code></pre>
	 *
	 * <p>Instead of code snippet, it can return a String instance if
	 * the validator is zul.inp.SimpleConstraint.
	 *
	 * <p>For example,
	 * <pre><code>String getClientConstraint() {
	 *  return "'no empty'";
	 *}</code></pre>
	 *
	 * <p>The validator coule implement the validate and showCustomError
	 * methods as follow. <code>validate</code> is required,
	 * while <code>showCustomError</code> is optional.
	 *
	 * <pre><code>String validate(Widget wgt, String value);</code></pre>
	 * <pre><code>Object showCustomError(Widget wgt, String errmsg);</code></pre>
	 *
	 * <p>Refer to <a href="http://docs.zkoss.org/wiki/Zul.inp.InputWidget#setConstraint">zul.inpu.InputWidget#setContraint</a>
	 * <a href="http://docs.zkoss.org/wiki/Zul.inp.SimpleConstraint">zul.inp.SimpleConstraint</a>
	 * for details.
	 *
	 * <p>Notice that, since 5.0.0, {@link ClientConstraint} has the higher priority than
	 * {@link CustomConstraint}. In other words, {@link CustomConstraint}
 	 * is ignored if both defined.
	 *
	 * @return the code snippet that will be evaluated at client to
	 * return a validator.
	 * @since 5.0.0
	 */
	public String getClientConstraint();
	/** Returns a list of packages separated by comma that ZK client
	 * engine has to load before evaluating {@link #getClientConstraint}.
	 * <p>For example,
	 * <pre><code>com.foo,com.foo.more</code></pre>
	 * @since 5.0.0
	 */
	public String getClientPackages();
}
