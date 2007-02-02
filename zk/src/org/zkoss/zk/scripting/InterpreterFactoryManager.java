/* InterpreterFactoryManager.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  2 10:48:47     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;

/**
 * The manager of interpreter factories ({@link InterpreterFactory}).
 *
 * <p>Application developers and deployers rarely need to access this
 * class directly. Rather, they can declare the interpreter factory in lang-addon.xml
 * if the factory is available to all Web applications. Alternatively,
 * they can declare the interpreter factory in WEB-INF/zk.xml if the factory
 * is available only the specific Web application.
 *
 * @author tomyeh
 */
public class InterpreterFactoryManager {
	private static final InterpreterFactoryManager _manager
		= new InterpreterFactoryManager();

	/** Returns the singleton of the manager for interpreter factories.
	 */
	public static final InterpreterFactoryManager the() {
		return _manager;
	}
	/** Constructor.
	 */
	protected InterpreterFactoryManager() {
	}

	/** Adds an interpreter factory.
	 * If wapp is specified, the factory is associated with the specified
	 * {@link WebApp}. Otherwise, the factory is visible to all Web applications.
	 *
	 * @param wapp the Web application that the factory belongs to.
	 * If null, the factory is visible to all Web application.
	 * @param name the name of the factory. It is case insensitive.
	 */
	public InterpreterFactory addFactory(
	WebApp wapp, String name, InterpreterFactory fty) {
		if (name == null || name.length() == 0 || fty == null)
			throw new IllegalArgumentException("emty or null");

		return null;
	}
	/** Returns the interpter factory of the specified name of the specified
	 * Web application.
	 *
	 * @exception InterpreterNotFoundException if not found.
	 */
	public InterpreterFactory getFactory(WebApp wap, String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException(name);

		throw new InterpreterNotFoundException(name, MZk.INTERPRETER_NOT_FOUND, name);
	}
}
