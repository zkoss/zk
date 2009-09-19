/* Evaluators.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 14 12:24:23     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.util;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.net.URL;

import org.zkoss.lang.Classes;
import org.zkoss.lang.SystemException;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.util.IDOMs;

/**
 * It mapps a name with an evaluator implementation.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class Evaluators {
	private static final Log log = Log.lookup(Evaluators.class);

	/** Map(name, Class/String class); */
	private static final Map _evals = new HashMap(8);
	private static boolean _loaded;

	private Evaluators() {} //prevent from being instantiated

	/** Returns the implementation for the specified evaluator name.
	 *
	 * @param name the name of the evaluator, say, MVEL.
	 * @exception SystemException if not found or the class not found.
	 */
	public static final Class getEvaluatorClass(String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("empty or null");

		if (!_loaded) load();

		final String evalnm = name.toLowerCase();
		final Object clsnm;
		synchronized (_evals) {
			clsnm = _evals.get(evalnm);
		}
		if (clsnm == null)
			throw new SystemException("Evaluator not found: " + name);

		if (clsnm instanceof Class) {
			return (Class)clsnm;
		} else {
			try {
				return Classes.forNameByThread((String)clsnm);
			} catch (ClassNotFoundException ex) {
				throw new SystemException("Failed to load class "+clsnm);
			}
		}
	}
	/** Tests whether the evaluator (aka., the expression factory)
	 * for the specified evaluator name
	 * exists.
	 *
	 * @param name the name of the evaluator, say, MVEL.
	 */
	public static final boolean exists(String name) {
		if (name == null) return false;

		if (!_loaded) load();

		name = name.toLowerCase();
		synchronized (_evals) {
			return _evals.containsKey(name);
		}
	}

	/** Adds an evaluator
	 * (aka., the expression factory, {@link org.zkoss.xel.ExpressionFactory}).
	 *
	 * @param name the name of the evaluator, say, MVEL.
	 * It is case insensitive.
	 * @param evalcls the class name of the evaluator, aka., the expression factory
	 * ({@link org.zkoss.xel.ExpressionFactory}).
	 * @return the previous class name, or null if not defined yet
	 */
	public static final String add(String name, String evalcls) {
		if (name == null || name.length() == 0
		|| evalcls == null || evalcls.length() == 0)
			throw new IllegalArgumentException("emty or null");

		if (log.debugable()) log.debug("Evaluator is added: "+name+", "+evalcls);

		final String evalnm = name.toLowerCase();
		final Object old;
		synchronized (_evals) {
			old = _evals.put(evalnm, evalcls);
		}

		return old instanceof Class ? ((Class)old).getName(): (String)old;
	}
	/** Adds an evaluator based on the XML declaration.
	 * The evaluator is also known as the expression factory,
	 * {@link org.zkoss.xel.ExpressionFactory}.
	 *
	 * <pre><code>
&lt;xel-config&gt;
  &lt;evaluator-name&gt;SuperEL&lt;/evaluator-name&gt;&lt;!-- case insensitive --!&gt;
  &lt;evaluator-class&gt;my.MySuperEvaluator&lt;/evaluator-class&gt;
&lt;/xel-config&gt;
	 * </code></pre>
	 *
	 * @param config the XML element called zscript-config
	 * @return the previous class, or null if not defined yet
	 */
	public static final String add(Element config) {
		//Spec: it is OK to declare an nonexist factory, since
		//deployer might remove unused jar files.
		final String name =
			IDOMs.getRequiredElementValue(config, "evaluator-name");
		final String clsnm =
			IDOMs.getRequiredElementValue(config, "evaluator-class");
		return add(name, clsnm);
	}

	/** Loads from metainfo/xel/config
	 */
	synchronized static final void load() {
		if (_loaded)
			return;

		try {
			final ClassLocator loc = new ClassLocator();
			for (Enumeration en = loc.getResources("metainfo/xel/config.xml");
			en.hasMoreElements();) {
				final URL url = (URL)en.nextElement();
				if (log.debugable()) log.debug("Loading "+url);
				try {
					final Document doc = new SAXBuilder(false, false, true).build(url);
					if (IDOMs.checkVersion(doc, url))
						parseConfig(doc.getRootElement(), loc);
				} catch (Exception ex) {
					log.error("Failed to parse "+url, ex); //keep running
				}
			}
		} catch (Exception ex) {
			log.error(ex); //keep running
		} finally {
			_loaded = true;
		}
	}
	/** Parse config.xml. */
	private static void parseConfig(Element root, Locator loc) {
		for (Iterator it = root.getElements("xel-config").iterator();
		it.hasNext();) {
			add((Element)it.next());
		}
	}
}
