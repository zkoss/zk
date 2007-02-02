/* InterpreterFactories.java

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

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;

import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;
import org.zkoss.idom.Element;
import org.zkoss.idom.util.IDOMs;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.UiException;

/**
 * The utilities to access interpreter factories ({@link InterpreterFactory}).
 *
 * <p>Application developers and deployers rarely need to access this
 * class directly. Rather, they can declare the interpreter factory in
 * either lang-addon.xml or WEB-INF/zk.xml.
 *
 * @author tomyeh
 */
public class InterpreterFactories {
	private static final Log log = Log.lookup(InterpreterFactories.class);

	/** Map(zslang, factory); */
	private static final Map _ftys = new HashMap();
	/** A set of language names. */
	private static final Set _zslangs = Collections.synchronizedSet(new LinkedHashSet());

	private InterpreterFactories() { //disable it
	}

	/** Returns the interpter factory of the specified language name.
	 *
	 * @param zslang the name of the scripting language, say, Java.
	 * @exception InterpreterNotFoundException if not found.
	 */
	public static final InterpreterFactory lookup(String zslang) {
		if (zslang == null || zslang.length() == 0)
			throw new IllegalArgumentException(zslang);

		zslang = zslang.toLowerCase();
		final InterpreterFactory fty;
		synchronized (_ftys) {
			fty = (InterpreterFactory)_ftys.get(zslang);
		}
		if (fty == null)
			throw new InterpreterNotFoundException(zslang, MZk.INTERPRETER_NOT_FOUND, zslang);
		return fty;
	}
	/** Returns a set of names of the scripting languages supported by this
	 * installation.
	 */
	public static final Set getZScriptLanguages() {
		return _zslangs;
	}

	/** Adds an interpreter factory.
	 *
	 * @param zslang the name of the scripting language, say, Java.
	 * It is case insensitive.
	 * @return the previous factory, or null if not defined yet
	 */
	public static final
	InterpreterFactory add(String zslang, InterpreterFactory fty) {
		if (zslang == null || zslang.length() == 0 || fty == null)
			throw new IllegalArgumentException("emty or null");

		if (log.debugable()) log.debug("Scripting language is added: "+zslang+", "+fty);
		_zslangs.add(zslang);

		zslang = zslang.toLowerCase();
		synchronized (_ftys) {
			return (InterpreterFactory)_ftys.put(zslang, fty);
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
	public static final InterpreterFactory add(Element config) {
		final String zslang =
			IDOMs.getRequiredElementValue(config, "language-name");
		final String clsnm =
			IDOMs.getRequiredElementValue(config, "factory-class");
		try {
			final Class cls = Classes.forNameByThread(clsnm);
			return add(zslang, (InterpreterFactory)cls.newInstance());
		} catch (Throwable ex) {
			throw new UiException("Unable to load "+clsnm+", at "+config.getLocator(), ex);
		}
	}
}
