/* JRubyInterpreter.java

	Purpose:
		
	Description:
		
	History:
		Sat Feb 10 19:56:11     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.jruby;

import java.util.Iterator;

import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.javasupport.JavaObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.IAccessor;
import org.jruby.runtime.GlobalVariable;
import org.jruby.runtime.Block;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.internal.runtime.GlobalVariables;

import org.zkoss.xel.Function;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.util.GenericInterpreter;

/**
 * JRuby interpreter.
 *
 * @author tomyeh
 */
public class JRubyInterpreter extends GenericInterpreter {
	private Ruby _runtime;

	/** Returns the native interpreter, or null if it is not initialized
	 * or destroyed.
	 * From application's standpoint, it never returns null, and the returned
	 * object must be an instance of {@link org.jruby.Ruby}
	 * @since 3.0.2
	 */
	public Object getNativeInterpreter() {
		return _runtime;
	}

	//GenericInterpreter//
	protected void exec(String script) {
		_runtime.evalScriptlet(script);
	}

	protected boolean contains(String name) {
		return _runtime.getGlobalVariables().isDefined(
			GlobalVariable.variableName(name));
	}
	protected Object get(String name) {
		IRubyObject ro = _runtime.getGlobalVariables().get(
			GlobalVariable.variableName(name));
		return rubyToJava(ro);
	}
	protected void set(String name, Object value) {
		_runtime.getGlobalVariables().define(
			GlobalVariable.variableName(name), new Variable(value));
	}
	protected void unset(String name) {
		_runtime.getGlobalVariables().set(
			GlobalVariable.variableName(name), _runtime.getNil());
	}

	//Interpreter//
	public void init(Page owner, String zslang) {
		super.init(owner, zslang);

		_runtime = Ruby.newInstance();
		_runtime.setGlobalVariables(new Variables(_runtime));
	}
	public void destroy() {
		JavaEmbedUtils.terminate(_runtime);
		_runtime = null;

		super.destroy();
	}

	/**TODO: need to digg out a solution from jruby's manual
	public Class getClass(String clsnm) {
	}
	public Function getFunction(String name, Class[] argTypes) {
	}
	*/

	//utilities//
	private IRubyObject javaToRuby(Object value) {
		IRubyObject ro = JavaUtil.convertJavaToRuby(_runtime, value);
		if (ro instanceof JavaObject)
			return _runtime.getModule("JavaUtilities")
				.callMethod(_runtime.getCurrentContext(), "wrap", ro);
		return ro;
	}
	private Object rubyToJava(IRubyObject value) {
		return JavaUtil.convertRubyToJava(value, Object.class);
	}

	/** The global scope. */
	private class Variables extends GlobalVariables {
		private Variables(Ruby runtime) {
			super(runtime);

			//we have to copy variables from the origin one to this
			GlobalVariables vars = runtime.getGlobalVariables();
			for (Iterator it = vars.getNames().iterator(); it.hasNext();) {
				final String nm = (String)it.next();
				set(nm, vars.get(nm));
			}
		}

		public IRubyObject get(String name) {
			IRubyObject ro = super.get(name);
			if (ro == _runtime.getNil()) {
				if (name.length() > 1 && name.charAt(0) == '$') //just in case
					name = name.substring(1);
				Object val = getFromNamespace(name);
				if (val != UNDEFINED) 
					return javaToRuby(val);
			}
			return ro;
		}
	}
	/*ruby variable*/
	private class Variable implements IAccessor {
		private Object _value;
	
		public Variable(Object value) {
			_value = value;
		}
	
		public IRubyObject getValue() {
			return javaToRuby(_value);
		}	
		public IRubyObject setValue(IRubyObject value) {
			_value = rubyToJava(value);
			return value;
		}
	}
}
