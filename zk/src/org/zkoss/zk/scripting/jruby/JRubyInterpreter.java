/* JRubyInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Feb 10 19:56:11     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.jruby;

import org.jruby.IRuby;
import org.jruby.Ruby;
import org.jruby.internal.runtime.GlobalVariables;
import org.jruby.javasupport.JavaEmbedUtils;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.Method;
import org.zkoss.zk.scripting.util.GenericInterpreter;

/**
 * JRuby interpreter.
 *
 * @author tomyeh
 */
public class JRubyInterpreter extends GenericInterpreter {
	private IRuby _runtime;

	//GenericInterpreter//
	protected void exec(String script) {
		_runtime.evalScript(script);
		_runtime.setGlobalVariables(new Variables(_runtime));
	}

	protected Object get(String name) {
		return null;
	}
	protected void set(String name, Object value) {
	}
	protected void unset(String name) {
	}

	//Interpreter//
	public void init(Page owner, String zslang) {
		super.init(owner, zslang);
		_runtime = Ruby.getDefaultInstance();
	}
	public void destroy() {
    	JavaEmbedUtils.terminate(_runtime);
        _runtime = null;

		super.destroy();
	}
	private class Variables extends GlobalVariables {
		private Variables(IRuby runtime) {
			super(runtime);
		}
	}
}
