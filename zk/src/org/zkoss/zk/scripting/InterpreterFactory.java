/* InterpreterFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  2 10:43:36     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;

/**
 * The interpreter dependent factory to create {@link Interpreter} and
 * {@link Namespace}.
 *
 * <p>To add a new interpreter, a developer has to implement this interface
 * and add the implementation class to {@link InterpreterFactoryManager}, which
 * can be done automatically by adding a declaration to zk.xml or lang-addon.xml.
 *
 * @author tomyeh
 */
public interface InterpreterFactory {
	/** Creates an interpreter.
	 *
	 * @param owner the page that the returne interpreter belongs to.
	 */
	public Interpreter newInterpreter(Page owner);
	/** Creates a namespace with the specified identifier
	 * for the specified component.
	 *
	 * @param owner the component that the returned namespace belongs to.
	 * @param id the identifer (never null).
	 */
	public Namespace newNamespace(Component owner, String id);
}
