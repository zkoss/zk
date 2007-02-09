/* RhinoInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Feb  7 23:01:34     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsf.rhino;

import org.mozilla.javascript.Context;

import org.zkoss.zk.scripting.bsf.BSFInterpreter;
import org.zkoss.zk.scripting.Namespace;

/**
 * The interpreter based on Rhino.
 *
 * @author tomyeh
 */
public class RhinoInterpreter extends BSFInterpreter {
	/** Constructs a Rhino-based interpreter.
	 */
	public RhinoInterpreter() {
		super("JavaScript",
			"org.apache.bsf.engines.javascript.JavaScriptEngine");
	}

	//NamespacelessInterpreter//
	protected void set(String name, Object value) {
		//Rhino doesn't allow to set the null value, so we unset it instead
		if (value != null) super.set(name, value);
		else unset(name);
	}

	//Note: we have to prepare context since GenericInterpreter.interpret
	//will set variables before calling exec. And, it will cause exception
	//if the context is not ready (since Rhino will wrap value with its
	//own class)
	protected void beforeExec() {
		Context.enter();
	}
	protected void afterExec() {
		Context.exit();
	}
}
