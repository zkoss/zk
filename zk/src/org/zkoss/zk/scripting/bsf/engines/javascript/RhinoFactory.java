/* RhinoFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Feb  6 14:38:42     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsf.engines.javascript;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.scripting.InterpreterFactory;
import org.zkoss.zk.scripting.Interpreter;

/**
 * The interpreter factory for BSF's JavaScript engine (Rhino).
 *
 * @author tomyeh
 */
public class RhinoFactory implements InterpreterFactory {
	//InterpreterFactory//
	public Interpreter newInterpreter(Page owner) {
		return new RhinoInterpreter("JavaScript",
			"org.apache.bsf.engines.javascript.JavaScriptEngine");
	}
}
