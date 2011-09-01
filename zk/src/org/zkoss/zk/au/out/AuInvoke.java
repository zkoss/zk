/* AuInvoke.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul  6 22:52:34     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
	/** Construct AuInvoke to call the peer widget's member function with
	 * no argument.
	 *
	 * @param comp the component that the widget is associated with.
	 * It cannot be null.
	 * @param function the function name
	 */
	public AuInvoke(Component comp, String function) {
		super("invoke", comp, new String[] {comp.getUuid(), function});
	}
	/** Construct AuInvoke to call the peer widget's member function with
	 * one argument.
	 *
	 * <p>Notice that if you want to pass an array-type argument, you have to cast
	 * it to Object as follows:<br/>
	 * <code>new AuInvoke(comp, "setOverride", (Object)new Object[] {"a", "b"})</code>.<br/>
	 * Otherwise, the third argument will be handled by
	 * {@link #AuInvoke(Component, String, Object[])}, and then considered
	 * as an array of arguments (rather than an argument with an array-type value.
	 *
	 * @param comp the component that the widget is associated with.
	 * It cannot be null.
	 * @param function the function name
	 * @param arg the additional argument. It could be null, String, Date,
	 * {@link org.zkoss.zk.ui.util.DeferredValue},
	 * and any kind of objects that
	 * the client accepts (marshaled by JSON).
	 * @since 5.0.0
	 */
	public AuInvoke(Component comp, String function, Object arg) {
		super("invoke", comp,
			new Object[] {comp.getUuid(), function, arg});
	}
	/** Construct AuInvoke to call the peer widget's member function with
	 * one boolean argument.
	 *
	 * @param comp the component that the widget is associated with.
	 * It cannot be null.
	 * @param function the function name
	 * @param arg the additional argument.
	 * Different devices might support more types.
	 * @since 5.0.0
	 */
	public AuInvoke(Component comp, String function, boolean arg) {
		super("invoke", comp,
			new Object[] {comp.getUuid(), function, Boolean.valueOf(arg)});
	}
	/** Construct AuInvoke to call the peer widget's member function with
	 * one int argument.
	 *
	 * @param comp the component that the widget is associated with.
	 * It cannot be null.
	 * @param function the function name
	 * @param arg the additional argument.
	 * @since 5.0.0
	 */
	public AuInvoke(Component comp, String function, int arg) {
		super("invoke", comp,
			new Object[] {comp.getUuid(), function, new Integer(arg)});
	}
	/** Construct AuInvoke to call the peer widget's member function with
	 * one double argument.
	 *
	 * @param comp the component that the widget is associated with.
	 * It cannot be null.
	 * @param function the function name
	 * @param arg the additional argument.
	 * @since 5.0.0
	 */
	public AuInvoke(Component comp, String function, double arg) {
		super("invoke", comp,
			new Object[] {comp.getUuid(), function, new Double(arg)});
	}
	/** Construct AuInvoke to call the peer widget's member function with
	 * two arguments.
	 *
	 * @param comp the component that the widget is associated with.
	 * It cannot be null.
	 * @param function the function name
	 * @param arg1 the additional argument. It could be null, String, Date,
	 * {@link org.zkoss.zk.ui.util.DeferredValue},
	 * and any kind of objects that
	 * the client accepts (marshaled by JSON).
	 * @param arg2 the 2nd additional argument.
	 * @since 5.0.0
	 */
	public AuInvoke(Component comp, String function, Object arg1, Object arg2) {
		super("invoke", comp, new Object[] {comp.getUuid(), function,
			arg1, arg2});
	}
	/** Construct AuInvoke to call the peer widget's member function with
	 * three arguments.
	 *
	 * @param comp the component that the widget is associated with.
	 * It cannot be null.
	 * @param function the function name
	 * @param arg1 the additional argument. It could be null, String, Date,
	 * {@link org.zkoss.zk.ui.util.DeferredValue},
	 * and any kind of objects that
	 * the client accepts (marshaled by JSON).
	 * @param arg2 the 2nd additional argument.
	 * @param arg3 the 3rd additional argument.
	 * @since 5.0.0
	 */
	public AuInvoke(Component comp, String function, Object arg1, Object arg2,
	Object arg3) {
		super("invoke", comp, new Object[] {comp.getUuid(), function,
			arg1, arg2, arg3});
	}
	/** Construct AuInvoke to call the peer widget's member function with
	 * an array of arguments.
	 *
	 * @param comp the component that the widget is associated with.
	 * It cannot be null.
	 * @param function the function name
	 * @param args the additional arguments. It could be null, String, Date,
	 * {@link org.zkoss.zk.ui.util.DeferredValue},
	 * and any kind of objects that
	 * the client accepts (marshaled by JSON since 5.0.0).
	 * @since 3.6.1
	 */
	public AuInvoke(Component comp, String function, Object[] args) {
		super("invoke", comp, toData(comp, function, args));
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
