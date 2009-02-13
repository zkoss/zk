/* Interpreters.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  2 10:48:47     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;

/**
 * The utilities to access interpreters ({@link Interpreter}).
 *
 * <p>Application developers and deployers rarely need to access this
 * class directly. Rather, they can declare the interpreter class in
 * either zk/config.xml or WEB-INF/zk.xml.
 *
 * @author tomyeh
 */
public class Interpreters {
	private static final Log log = Log.lookup(Interpreters.class);

	/** Map(zslang, Class/String class); */
	private static final Map _ips = new HashMap();
	/** A set of language names. */
	private static final Set _zslangs =
		Collections.synchronizedSet(new LinkedHashSet());

	private Interpreters() { //disable it
	}

	/** Returns the interpter for the specified language name.
	 *
	 * @param zslang the name of the scripting language, say, Java.
	 * @exception InterpreterNotFoundException if not found.
	 */
	public static final Interpreter newInterpreter(String zslang, Page owner) {
		if (zslang == null || zslang.length() == 0 || owner == null)
			throw new IllegalArgumentException("empty or null");

		final String zsl = zslang.toLowerCase();
		final Object clsnm;
		synchronized (_ips) {
			clsnm = _ips.get(zsl);
		}
		if (clsnm == null)
			throw new InterpreterNotFoundException(zslang, MZk.NOT_FOUND, zslang);

		final Class cls;
		if (clsnm instanceof Class) {
			cls = (Class)clsnm;
		} else {
			try {
				cls = Classes.forNameByThread((String)clsnm);
			} catch (ClassNotFoundException ex) {
				throw new UiException("Failed to load class "+clsnm);
			}
			if (!Interpreter.class.isAssignableFrom(cls))
				throw new IllegalArgumentException(cls+" must implements "+Interpreter.class);

			synchronized (_ips) {
				final Object old = _ips.put(zsl, cls);
				if (old != clsnm)
					_ips.put(zsl, old); //changed by someone else; so restore
			}
		}

		try {
			final Interpreter ip = (Interpreter)cls.newInstance();
			ip.init(owner, zslang);
			return ip;
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex, "Unable to create "+cls);
		}
	}
	/** Tests whether the interpreter for the specified language name
	 * exists.
	 *
	 * @param zslang the name of the scripting language, say, Java.
	 */
	public static final boolean exists(String zslang) {
		if (zslang == null) return false;

		zslang = zslang.toLowerCase();
		synchronized (_ips) {
			return _ips.containsKey(zslang);
		}
	}
	/** Returns a set of names of the scripting languages supported by this
	 * installation.
	 */
	public static final Set getZScriptLanguages() {
		return _zslangs;
	}

	/** Adds an interpreter class.
	 *
	 * @param zslang the name of the scripting language, say, Java.
	 * It is case insensitive.
	 * @param ipcls the class name of interpreter ({@link Interpreter}).
	 * @return the previous class name, or null if not defined yet
	 */
	public static final String add(String zslang, String ipcls) {
		if (zslang == null || zslang.length() == 0
		|| ipcls == null || ipcls.length() == 0)
			throw new IllegalArgumentException("emty or null");

		for (int j = zslang.length();  --j >= 0;) {
			final char cc = zslang.charAt(j);
			if (!isLegalName(cc))
				throw new IllegalArgumentException('\''+cc+"' not allowed in a language name, "+zslang);
		}

		if (log.debugable()) log.debug("Scripting language is added: "+zslang+", "+ipcls);
		_zslangs.add(zslang);

		final String zsl = zslang.toLowerCase();
		final Object old;
		synchronized (_ips) {
			old = _ips.put(zsl, ipcls);
		}

		return old instanceof Class ? ((Class)old).getName(): (String)old;
	}
	/** Tests whether a character is legal to be used as part of the scripting
	 * language name.
	 */
	public static boolean isLegalName(char cc) {
		return (cc >= 'a' && cc <= 'z') || (cc >= 'A' && cc <= 'Z')
			|| (cc >= '0' && cc <= '9') || cc == '_';
	}
	/** Adds an interpreter based on the XML declaration.
	 *
	 * <pre><code>
&lt;zscript-config&gt;
  &lt;language-name&gt;SuperJava&lt;/language-name&gt;&lt;!-- case insensitive --!&gt;
  &lt;interpreter-class&gt;my.MySuperJavaInterpreter&lt;/interpreter-class&gt;
&lt;/zscript-config&gt;
	 * </code></pre>
	 *
	 * @param config the XML element called zscript-config
	 * @return the previous class, or null if not defined yet
	 */
	public static final String add(Element config) {
		//Spec: it is OK to declare an nonexist interpreter, since
		//deployer might remove unused jar files.
		final String zslang =
			IDOMs.getRequiredElementValue(config, "language-name");
		final String clsnm =
			IDOMs.getRequiredElementValue(config, "interpreter-class");
		return add(zslang, clsnm);
	}
}
