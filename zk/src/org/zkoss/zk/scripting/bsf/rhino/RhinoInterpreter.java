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
	 *
	 * @param lang the language, say, Groovy.
	 * @param engineClassnm the class name of BSF engine
	 */
	public RhinoInterpreter(String lang, String engineClassnm) {
		super(lang, engineClassnm);
	}

	//NamespacelessInterpreter//
	protected void setVariable(String name, Object value) {
		//Rhino doesn't allow to set the null value, so we unset it instead
		if (value != null) super.setVariable(name, value);
		else unsetVariable(name);
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
