/* RhinoFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  9 00:23:57     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.rhino;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.scripting.InterpreterFactory;
import org.zkoss.zk.scripting.Interpreter;

/**
 * The interpreter factory for Rhino - JavaScript interpreter.
 * 
 * @author tomyeh
 */
public class RhinoFactory implements InterpreterFactory {
	public Interpreter newInterpreter(Page owner) {
		return new RhinoInterpreter(owner);
	}
}
