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

import java.util.Map;
import java.util.HashMap;

import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;
import org.zkoss.idom.Element;
import org.zkoss.idom.util.IDOMs;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.UiException;

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
	private static final Log log = Log.lookup(InterpreterFactoryManager.class);
	private static final InterpreterFactoryManager _manager
		= new InterpreterFactoryManager();

	/** Map(wapp, Map(name, factory)); */
	private final Map _ftys = new HashMap();

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
	 * @return the previous factory, or null if not defined yet
	 */
	public InterpreterFactory add(WebApp wapp, String name, InterpreterFactory fty) {
		if (name == null || name.length() == 0 || fty == null)
			throw new IllegalArgumentException("emty or null");

		if (log.debugable()) log.debug("Scripting language is added: "+name+", "+fty);
		name = name.toLowerCase();

		Map map;
		synchronized (_ftys) {
			map = (Map)_ftys.get(wapp);
			if (map == null)
				_ftys.put(wapp, map = new HashMap());
		}
		synchronized (map) {
			return (InterpreterFactory)map.put(name, fty);
		}
	}
	/** Adds an interpreter factory based on the XML declaration.
	 *
	 * <pre><code>
&lt;zscript-config&gt;
  &lt;zscript-language&gt;
    &lt;language-name&gt;SuperJava&lt;/language-name&gt;&lt;!-- case insensitive --!&gt;
    &lt;factory-class&gt;my.MySuperJavaInterpreterFactory&lt;/factory-class&gt;
  &lt;/zscript-language&gt;
&lt;/zscript-config&gt;
	 * </code></pre>
	 *
	 * @param config the XML element called zscript-config
	 * @return the previous factory, or null if not defined yet
	 */
	public InterpreterFactory add(WebApp wapp, Element config) {
		final String name =
			IDOMs.getRequiredElementValue(config, "language-name");
		final String clsnm =
			IDOMs.getRequiredElementValue(config, "factory-class");
		try {
			final Class cls = Classes.forNameByThread(clsnm);
			return add(wapp, name, (InterpreterFactory)cls.newInstance());
		} catch (Throwable ex) {
			throw new UiException("Unable to load "+clsnm+", at "+config.getLocator(), ex);
		}
	}

	/** Returns the interpter factory of the specified name of the specified
	 * Web application.
	 *
	 * @exception InterpreterNotFoundException if not found.
	 */
	public InterpreterFactory get(WebApp wapp, String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException(name);

		name = name.toLowerCase();

		Map map;
		synchronized (_ftys) {
			map = (Map)_ftys.get(wapp);
		}
		if (map != null) {
			synchronized (map) {
				final InterpreterFactory fty = (InterpreterFactory)map.get(name);
				if (fty != null)
					return fty;
			}
		}

		if (wapp == null)
			throw new InterpreterNotFoundException(name, MZk.INTERPRETER_NOT_FOUND, name);
		return get(null, name);
	}
	/** Clears the definitions of the specified Web application.
	 * It must be called when a Web application is about to be destroyed.
	 */
	public void clear(WebApp wapp) {
		synchronized (_ftys) {
			_ftys.remove(wapp);
		}
	}
}
