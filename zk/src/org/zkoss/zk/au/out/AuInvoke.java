/* AuInvoke.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  6 22:52:34     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to execute the specified client function.
 * Unlike {@link AuScript}, it invokes the function called zkType.function,
 * where Type is the component's type (at the client) and function
 * is the function name specified in {@link AuInvoke}.
 *
 * <p>data[0]: the component UUID<br/>
 * data[1]: the client function name (i.e., JavaScript function)<br/>
 * data[2]: the second argument<br/>
 * data[3]: the third argument...
 *
 * <p>Note: the first argument is always the component itself.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuInvoke extends AuResponse {
	/** Construct AuInvoke to call a client function with one argument,
	 * the component itself.
	 *
	 * <p>In other words, it invokes (assume Type is the comp's z.type)<br/>
	 * <code>zkType.function(comp)</code>
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 */
	public AuInvoke(Component comp, String function) {
		super("invoke", comp, new String[] {comp.getUuid(), function});
	}
	/** Construct AuInvoke to call a client function with two arguments,
	 * the component itself and arg.
	 *
	 * <p>In other words, it invokes (assume Type is the comp's z.type)<br/>
	 * <code>zkType.function(comp, arg)</code>
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 * @param arg the additional argument
	 */
	public AuInvoke(Component comp, String function, String arg) {
		super("invoke", comp,
			new String[] {comp.getUuid(), function, arg});
	}
	/** Construct AuInvoke to call a client function with two arguments,
	 * the component itself and arg.
	 *
	 * <p>In other words, it invokes (assume Type is the comp's z.type)<br/>
	 * <code>zkType.function(comp, arg)</code>
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 * @param arg the additional argument. "false" if false, "true" if true.
	 * @since 3.0.1
	 */
	public AuInvoke(Component comp, String function, boolean arg) {
		super("invoke", comp,
			new String[] {comp.getUuid(), function, arg ? "true": "false"});
	}
	/** Construct AuInvoke to call a client function with three arguments,
	 * the component itself, arg1 and arg2.
	 *
	 * <p>In other words, it invokes (assume Type is the comp's z.type)<br/>
	 * <code>zkType.function(comp, arg1, arg2)</code>
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 */
	public AuInvoke(Component comp, String function, String arg1, String arg2) {
		super("invoke", comp, new String[] {comp.getUuid(), function,
			arg1, arg2});
	}
	/** Construct AuInvoke to call a client function with four arguments,
	 * the component itself, arg1, arg2 and arg3.
	 *
	 * <p>In other words, it invokes (assume Type is the comp's z.type)<br/>
	 * <code>zkType.function(comp, arg1, arg2, arg3)</code>
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 */
	public AuInvoke(Component comp, String function, String arg1, String arg2,
	String arg3) {
		super("invoke", comp, new String[] {comp.getUuid(), function,
			arg1, arg2, arg3});
	}
	/** Construct AuInvoke to call a client function with variable number of
	 * arguments.
	 * Notice that the component itself will be inserted in front of
	 * the specified argument array. In other words, the first argument
	 * is the component itself, the second is the first item in the
	 * argument array, and so on.<br/>
	 * <code>zkType.function(comp, args[0], args[1], args[2]...)</code>
	 *
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 * @since 3.6.0
	 */
	public AuInvoke(Component comp, String function, String[] args) {
		super("invoke", comp, toData(comp, function, args));
	}
	private static
	String[] toData(Component comp, String function, String[] args) {
		if (args == null)
			return new String[] {comp.getUuid(), function};

		final String[] data = new String[args.length + 2];
		data[0] = comp.getUuid();
		data[1] = function;
		for (int j = 0; j < args.length; ++j)
			data[2 + j] = args[j];
		return data;
	}
}
