/* RhinoInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  9 00:23:47     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ImporterTopLevel;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.Method;
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

	//GenericInterpreter//
	protected void exec(String script) {
		Context.getCurrentContext()
			.evaluateString(_global, script, "zk", 1, null);
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
		Context.enter();
	}
	protected void afterExec() {
		Context.exit();
	}

	//Interpreter//
	public void init(Page owner) {
		super.init(owner);

		final Context ctx = Context.enter();
		try {
			_global = new GlobalLevel(ctx);
		} finally {
			Context.exit();
		}
	}

	/**TODO: feasible but need to read manual/source first
	public Class getClass(String clsnm) {
	}
	*/
	/** Returns the method.
	 * <p>Note: JavaScript identifies a function with the name only.
	 */
	public Method getMethod(String name, Class[] argTypes) {
		Context.enter();
		try {
			final Object val = _global.get(name, _global);
			if (!(val instanceof Function))
				return null;
			return new RhinoMethod((Function)val);
		} finally {
			Context.exit();
		}
	}

	//supporting class//
	/** Extends ImporterTopLevel to support ZK namespaces.
	 */
	private class GlobalLevel extends ImporterTopLevel {
		private GlobalLevel(Context ctx) {
			super(ctx);
		}
		/* Not sure the side effect yet, so disable it
		public boolean has(String name, Scriptable start) {
			return super.has(name, start) || getFromNamespace(name) != null;
		}*/
		public Object get(String name, Scriptable start) {
			final Object val = super.get(name, start);
			if (val == Scriptable.NOT_FOUND
			|| val == Undefined.instance) {
				final Object v = getFromNamespace(name);
				if (v != null) return toJS(v);
			}
			return val;
		}
	}
}
