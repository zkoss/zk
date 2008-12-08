/* AuInvoke.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  6 22:52:34     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
	 * @param arg the additional argument It could be null, String, Date,
	 * {@link org.zkoss.zk.ui.util.DeferredValue}, primitives
	 * (such as Boolean and {@link org.zkoss.zk.device.marshal.$boolean})
	 * and an array of above types.
	 * Different devices might support more types.
	 * @since 5.0.0
	 */
	public AuInvoke(Component comp, String function, Object arg) {
		super("invoke", comp,
			new Object[] {comp.getUuid(), function, arg});
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
	 * @param arg1 the additional argument It could be null, String, Date,
	 * {@link org.zkoss.zk.ui.util.DeferredValue}, primitives
	 * (such as Boolean and {@link org.zkoss.zk.device.marshal.$boolean})
	 * and an array of above types.
	 * Different devices might support more types.
	 * @param arg2 the 2nd additional argument.
	 * @since 5.0.0
	 */
	public AuInvoke(Component comp, String function, Object arg1, Object arg2) {
		super("invoke", comp, new Object[] {comp.getUuid(), function,
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
	 * @param arg1 the additional argument. It could be null, String, Date,
	 * {@link org.zkoss.zk.ui.util.DeferredValue}, primitives
	 * (such as Boolean and {@link org.zkoss.zk.device.marshal.$boolean})
	 * and an array of above types.
	 * Different devices might support more types.
	 * @param arg2 the 2nd additional argument.
	 * @param arg3 the 3rd additional argument.
	 * @since 5.0.0
	 */
	public AuInvoke(Component comp, String function, Object arg1, Object arg2,
	Object arg3) {
		super("invoke", comp, new Object[] {comp.getUuid(), function,
			arg1, arg2, arg3});
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
	 * @param args the additional arguments. It could be null, String, Date,
	 * {@link org.zkoss.zk.ui.util.DeferredValue}, primitives
	 * (such as Boolean and {@link org.zkoss.zk.device.marshal.$boolean})
	 * and an array of above types.
	 * Different devices might support more types.
	 * @since 5.0.0
	 */
	public AuInvoke(Component comp, String function, Object[] args) {
		super("invoke", comp, toData(comp, function, args));
	}
	private static final
	Object[] toData(Component comp, String function, Object[] args) {
		final Object[] data = new Object[2 + (args != null ? args.length: 0)];
		data[0] = comp.getUuid();
		data[1] = function;
		for (int j = 2; j < data.length; ++j)
			data[j] = args[j - 2];
		return data;
	}
}
