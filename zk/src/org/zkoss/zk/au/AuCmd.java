/* AuCmd.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  6 10:49:43     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import org.zkoss.zk.ui.Component;

/**
 * A response to ask the client to execute the specified client function.
 * Unlike {@link AuScript},
 *
 * <p>data[0]: the component UUID
 * data[1]: the client function name (i.e., JavaScript function)<br/>
 * data[2]: the second argument<br/>
 * data[3]: the third argument...
 *
 * <p>Note: the first argument is always the component itself.
 *
 * <p>Deprecated since 3.0.0. Use {@link AuInvoke} instead.
 * 
 * @author tomyeh
 * @deprecated
 */
public class AuCmd extends AuInvoke {
	/** Construct AuCmd to call a client function with one argument,
	 * the component itself.
	 *
	 * <p>In other words, it invokes (assume Type is the comp's z.type)<br/>
	 * <code>zkType.function(comp)</code>
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 * @deprecated
	 */
	public AuCmd(Component comp, String function) {
		super(comp, function);
	}
	/** Construct AuCmd to call a client function with two arguments,
	 * the component itself and arg.
	 *
	 * <p>In other words, it invokes (assume Type is the comp's z.type)<br/>
	 * <code>zkType.function(comp, arg)</code>
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 * @param arg the additional argument
	 * @deprecated
	 */
	public AuCmd(Component comp, String function, String arg) {
		super(comp, function, arg);
	}
	/** Construct AuCmd to call a client function with three arguments,
	 * the component itself, arg1 and arg2.
	 *
	 * <p>In other words, it invokes (assume Type is the comp's z.type)<br/>
	 * <code>zkType.function(comp, arg1, arg2)</code>
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 * @deprecated
	 */
	public AuCmd(Component comp, String function, String arg1, String arg2) {
		super(comp, function, arg1, arg2);
	}
	/** Construct AuCmd to call a client function with four arguments,
	 * the component itself, arg1, arg2 and arg3.
	 *
	 * <p>In other words, it invokes (assume Type is the comp's z.type)<br/>
	 * <code>zkType.function(comp, arg1, arg2, arg3)</code>
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 * @deprecated
	 */
	public AuCmd(Component comp, String function, String arg1, String arg2,
	String arg3) {
		super(comp, function, arg1, arg2, arg3);
	}
}
