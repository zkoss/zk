/* GroovyInterpreter.java

	Purpose:
		
	Description:
		
	History:
		Fri Feb  9 15:47:22     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.groovy;

import java.util.HashMap;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.InvokerHelper;

import org.zkoss.xel.Function;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.util.GenericInterpreter;

/**
 * Groovy interpreter.
 *
 * <p><a href="http://groovy.codehaus.org/">More about Groovy</a>.
 * 
 * @author tomyeh
 */
public class GroovyInterpreter extends GenericInterpreter {
	private Binding _global;
	private GroovyShell _ip;

	public GroovyInterpreter() {
	}

	/** Returns the top-level scope.
	 */
	/*package*/ Binding getGlobalScope() {
		return _global;
	}

	/** Returns the native interpreter, or null if it is not initialized
	 * or destroyed.
	 * From application's standpoint, it never returns null, and the returned
	 * object must be an instance of {@link groovy.lang.GroovyShell}
	 * @since 3.0.2
	 */
	public Object getNativeInterpreter() {
		return _ip;
	}

	//GenericInterpreter//
	protected void exec(String script) {
		_ip.evaluate(script);
	}

	protected boolean contains(String name) {
		return _global.getVariables().containsKey(name);
	}
	protected Object get(String name) {
		try {
			return _global.getVariable(name);
		} catch (groovy.lang.MissingPropertyException ex) { //Groovy throws exceptions instead of returning null
			return null;
		}
	}
	protected void set(String name, Object value) {
		_global.setVariable(name, value);
	}
	protected void unset(String name) {
		_global.getVariables().remove(name);
	}

	//Interpreter//
	public void init(Page owner, String zslang) {
		super.init(owner, zslang);

		_global = new Binding(new Variables());
		_ip = new GroovyShell(_global);
	}
	public void destroy() {
		_ip = null;
		_global = null;
		super.destroy();
	}

	/**TODO: need to digg out a solution from groovy's manual
	public Class getClass(String clsnm) {
	}
	*/
	/** Returns the method.
	 * <p>Currently it only looks for closures, and argTypes are ignored.
	 */
	public Function getFunction(String name, Class[] argTypes) {
		final Object val = get(name);
		if (!(val instanceof Closure))
			return null;
		return new ClosureFunction((Closure)val);
	}

	//supporting class//
	/** Extends Binding to support ZK namespaces.
	 */
	private class Variables extends HashMap {
		public Object get(Object key) {
			Object val = super.get(key);
			if (val != null || containsKey(key) || !(key instanceof String))
				return val;
			val = getFromNamespace((String)key);
			return val != UNDEFINED ? val: null;
		}
	}
	private static class ClosureFunction implements Function {
		private final Closure _closure;
		private ClosureFunction(Closure closure) {
			_closure = closure;
		}

		//-- Function --//
		public Class[] getParameterTypes() {
			return new Class[0];
		}
		public Class getReturnType() {
			return Object.class;
		}
		public Object invoke(Object obj, Object[] args) throws Exception {
			if (args == null) return _closure.call();
			else return _closure.call(args);
		}
		public java.lang.reflect.Method toMethod() {
			return null;
		}
	}
}
