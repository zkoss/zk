/* Evaluators.java

	Purpose:

	Description:

	History:
		Fri Sep 14 12:24:23     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.util;

import static org.zkoss.lang.Generics.cast;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;
import org.zkoss.lang.Classes;
import org.zkoss.lang.SystemException;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.util.resource.Locator;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;

/**
 * It maps a name with an evaluator implementation.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class Evaluators {
	private static final Logger log = LoggerFactory.getLogger(Evaluators.class);

	/** Map(name, Class/String class); */
	private static final Map<String, Object> _evals = new HashMap<String, Object>(8);
	private static boolean _loaded;

	private Evaluators() {} //prevent from being instantiated

	/** Returns the implementation for the specified evaluator name.
	 *
	 * @param name the name of the evaluator, say, MVEL.
	 * @exception SystemException if not found or the class not found.
	 */
	@SuppressWarnings("unchecked")
	public static final Class<? extends ExpressionFactory> getEvaluatorClass(String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("empty or null");

		if (!_loaded) load();

		final String evalnm = name.toLowerCase(java.util.Locale.ENGLISH);
		final Object clsnm;
		synchronized (_evals) {
			clsnm = _evals.get(evalnm);
		}
		if (clsnm == null)
			throw new SystemException("Evaluator not found: " + name);

		if (clsnm instanceof Class) {
			final Class<?> cls = (Class)clsnm;
			if (!ExpressionFactory.class.isAssignableFrom(cls))
				throw new SystemException(cls + " must implements " + ExpressionFactory.class);
			return cast(cls);
		} else {
			try {
				return cast(Classes.forNameByThread((String)clsnm));
			} catch (ClassNotFoundException ex) {
				throw new SystemException("Failed to load class "+clsnm);
			}
		}
	}
	/** Tests whether the evaluator (a.k.a., the expression factory)
	 * for the specified evaluator name
	 * exists.
	 *
	 * @param name the name of the evaluator, say, MVEL.
	 */
	public static final boolean exists(String name) {
		if (name == null) return false;

		if (!_loaded) load();

		name = name.toLowerCase(java.util.Locale.ENGLISH);
		synchronized (_evals) {
			return _evals.containsKey(name);
		}
	}

	/** Adds an evaluator
	 * (a.k.a., the expression factory, {@link ExpressionFactory}).
	 *
	 * @param name the name of the evaluator, say, MVEL.
	 * It is case insensitive.
	 * @param evalcls the class name of the evaluator, a.k.a., the expression factory
	 * ({@link ExpressionFactory}).
	 * @return the previous class name, or null if not defined yet
	 */
	public static final String add(String name, String evalcls) {
		if (name == null || name.length() == 0
		|| evalcls == null || evalcls.length() == 0)
			throw new IllegalArgumentException("emty or null");

		if (log.isDebugEnabled())
			log.debug("Evaluator is added: {}, {}", name, evalcls);

		final String evalnm = name.toLowerCase(java.util.Locale.ENGLISH);
		final Object old;
		synchronized (_evals) {
			old = _evals.put(evalnm, evalcls);
		}

		return old instanceof Class<?> ? ((Class<?>)old).getName(): (String)old;
	}
	/** Adds an evaluator based on the XML declaration.
	 * The evaluator is also known as the expression factory,
	 * {@link ExpressionFactory}.
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
				final URL url = (URL) en.nextElement();
				if (log.isDebugEnabled())
					log.debug("Loading {}", url);
				try {
					final Document doc = new SAXBuilder(false, false, true).build(url);
					if (IDOMs.checkVersion(doc, url))
						parseConfig(doc.getRootElement(), loc);
				} catch (Exception ex) {
					log.error("Failed to parse "+url, ex); //keep running
				}
			}
		} catch (Exception ex) {
			log.error("", ex); //keep running
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

	/** Resolves the variable based on the the specified context and
	 * variable resolver.
	 * @since 5.0.0
	 */
	public static Object resolveVariable(XelContext ctx,
	VariableResolver resolver, Object base, Object name) {
		if (resolver instanceof VariableResolverX) {
			if (ctx == null)
				ctx = new SimpleXelContext(resolver);
			return  ((VariableResolverX)resolver).resolveVariable(ctx, base, name);
		} else if (resolver != null && base == null && name != null)
			return resolver.resolveVariable(name.toString());
		return null;
	}
	/** Resolves the variable based on the specified context.
	 * If the variable resolver ({@link XelContext#getVariableResolver}
	 * is an instance of {@link VariableResolverX}, then
	 * {@link VariableResolverX#resolveVariable(XelContext,Object,Object)}
	 * will be invoked.
	 * @param ctx the context. If null, null will be returned.
	 * @since 5.0.0
	 */
	public static Object resolveVariable(XelContext ctx, Object base, Object name) {
		if (ctx != null) {
			VariableResolver resolver = ctx.getVariableResolver();
			if (resolver instanceof VariableResolverX)
				return  ((VariableResolverX)resolver).resolveVariable(ctx, base, name);
			else if (resolver != null && base == null && name != null)
				return resolver.resolveVariable(name.toString());
		}
		return null;
	}
	/** Resolves the variable based on the specified resolver.
	 * If the resolver is an instance of {@link VariableResolverX}, then
	 * {@link VariableResolverX#resolveVariable(XelContext,Object,Object)}
	 * will be invoked.
	 * <p>Notice that it is always better to invoke {@link #resolveVariable(XelContext,Object,Object)}
	 * if {@link XelContext} is available.
	 * @param resolver the variable resolver. If null, null will be returned.
	 * @since 5.0.0
	 */
	public static Object resolveVariable(VariableResolver resolver, String name) {
		return resolver != null ?
			resolveVariable(new SimpleXelContext(resolver), null, name): null;
	}
}
