/* RhinoInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  9 00:23:47     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ImporterTopLevel;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.util.GenericInterpreter;

/**
 * Rhino-based JavaScript interpreter.
 *
 * @author tomyeh
 */
public class RhinoInterpreter extends GenericInterpreter {
	private Scriptable _global;

	public RhinoInterpreter() {
	}

	/** Returns the top-level scope.
	 */
	/*package*/ Scriptable getGlobalScope() {
		return _global;
	}

	/** Returns the native interpreter, or null if it is not initialized
	 * or destroyed.
	 * From application's standpoint, it never returns null, and the returned
	 * object must be an instance of {@link org.mozilla.javascript.Scriptable}
	 * @since 3.0.2
	 */
	public Object getNativeInterpreter() {
		return _global;
	}

	//GenericInterpreter//
	protected void exec(String script) {
		Context.getCurrentContext()
			.evaluateString(_global, script, "zk", 1, null);
	}
	protected boolean contains(String name) {
		return _global.has(name, _global);
	}
	protected Object get(String name) {
		final Object val = _global.get(name, _global);
		if (val == Scriptable.NOT_FOUND
		|| val == Undefined.instance || val == null)
			return null;

		return Context.getCurrentContext()
			.jsToJava(val, ScriptRuntime.ObjectClass);
	}
	protected void set(String name, Object value) {
		_global.put(name, _global, toJS(value));
	}
	private Object toJS(Object value) {
		return value == null || (value instanceof Number) ||
			(value instanceof String) || (value instanceof Boolean) ?
				value: Context.toObject(value, _global);
	}
	protected void unset(String name) {
		_global.delete(name);
	}
	protected void beforeExec() {
		enterContext();
	}
	protected void afterExec() {
		exitContext();
	}
	private Context enterContext() {
		return ContextFactory.getGlobal().enterContext();
	}
	private void exitContext() {
		Context.exit();
	}

	//Interpreter//
	public void init(Page owner, String zslang) {
		super.init(owner, zslang);

		final Context ctx = enterContext();
		try {
			_global = new GlobalScope(ctx);
		} finally {
			exitContext();
		}
	}
	public void destroy() {
		_global = null;
		super.destroy();
	}

	/**TODO: feasible but need to read manual/source first
	public Class getClass(String clsnm) {
	}
	*/
	/** Returns the method.
	 * <p>Note: JavaScript identifies a function with the name only.
	 */
	public org.zkoss.xel.Function getFunction(String name, Class[] argTypes) {
		enterContext();
		try {
			final Object val = _global.get(name, _global);
			if (!(val instanceof Function))
				return null;
			return new RhinoFunction((Function)val);
		} finally {
			exitContext();
		}
	}

	//supporting class//
	/** Extends ImporterTopLevel to support ZK namespaces.
	 */
	private class GlobalScope extends ImporterTopLevel {
		private GlobalScope(Context ctx) {
			super(ctx);
		}
		/* Not sure the side effect yet, so disable it
		public boolean has(String name, Scriptable start) {
			return super.has(name, start) || getFromNamespace(name) != UNDEFINED;
		}*/
		public Object get(String name, Scriptable start) {
			final Object val = super.get(name, start);
			if (val == Scriptable.NOT_FOUND
			|| val == Undefined.instance) {
				final Object v = getFromNamespace(name);
				if (v != UNDEFINED) return toJS(v);
			}
			return val;
		}
	}

	private class RhinoFunction implements org.zkoss.xel.Function {
		private final Function _func;
		private RhinoFunction(Function func) {
			if (func == null)
				throw new IllegalArgumentException("null");
			_func = func;
		}

		//-- Function --//
		public Class[] getParameterTypes() {
			return new Class[0];
		}
		public Class getReturnType() {
			return Object.class;
		}
		public Object invoke(Object obj, Object[] args) throws Exception {
			final Context ctx = enterContext();
			try {
				final Scriptable scope = getGlobalScope();
				return _func.call(ctx, scope, scope, args);
			} finally {
				exitContext();
			}
		}
		public java.lang.reflect.Method toMethod() {
			return null;
		}
	}
}
