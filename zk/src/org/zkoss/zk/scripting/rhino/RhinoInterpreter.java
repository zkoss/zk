/* RhinoInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  9 00:23:47     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.rhino;

import org.mozilla.javascript.Context;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.scripting.util.GenericInterpreter;

/**
 * Rhino-based JavaScript interpreter.
 *
 * @author tomyeh
 */
public class RhinoInterpreter extends GenericInterpreter {
	public RhinoInterpreter(Page owner) {
		super(owner);
	}

	//GenericInterpreter//
	protected void exec(String script) {
	}
	protected Object getVariable(String name) {
		return null; //TODO
	}
	protected void beforeExec() {
		Context.enter();
	}
	protected void afterExec() {
		Context.exit();
	}

	//Interpreter//
	/**TODO: feasible but need to read manual/source first
	public Class getClass(String clsnm) {
	}
	public Method getMethod(String name, Class[] argTypes) {
	}
	*/
}
