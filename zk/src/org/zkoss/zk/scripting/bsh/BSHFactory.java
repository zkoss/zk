/* BSHFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  2 15:18:52     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsh;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.scripting.*;

/**
 * The interpreter factory for BeanShell.
 *
 * @author tomyeh
 */
public class BSHFactory implements InterpreterFactory {
	public Interpreter newInterpreter(Page owner) {
		return new BSHInterpreter();
	}
	public Namespace newNamespace(Component owner, String id) {
		return new BSHNamespace(id);
	}
}
