/* VariableResolverInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 18:12:56     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Map;

import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.VariableResolver;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.Evaluator;

/**
 * A definition of the variable resolver ({@link VariableResolver}).
 *
 * <p>Note: we resolve the class by use fo Classes.forNameByThread.
 * In other words, it doesn't support the class defined in zscript.
 * Why not? Since there is no way to run zscript before
 * the variable-resolver directive (and better performance).
 * </p>
 * 
 * @author tomyeh
 */
public class VariableResolverInfo extends ArgumentInfo {
//	private static final Log log = Log.lookup(VariableResolverInfo.class);

	/** A class, an ExValue or an VariableResolver. */
	private final Object _resolver;

	/** Constructs with a class.
	 * @since 3.6.2
	 */
	public VariableResolverInfo(Class<? extends VariableResolver> cls, Map<String, String> args) {
		super(args);
		checkClass(cls);
		_resolver = cls;
	}
	/** Constructs with a class.
	 */
	public VariableResolverInfo(Class<? extends VariableResolver> cls) {
		this(cls, null);
	}
	private static void checkClass(Class<?> cls) {
		if (!VariableResolver.class.isAssignableFrom(cls))
			throw new UiException(VariableResolver.class+" must be implemented: "+cls);
	}

	/** Constructs with a class name.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 * @since 3.6.2
	 */
	public VariableResolverInfo(String clsnm, Map<String, String> args)
	throws ClassNotFoundException {
		super(args);

		if (clsnm == null || clsnm.length() == 0)
			throw new IllegalArgumentException("empty");

		if (clsnm.indexOf("${") < 0) {
			try {
				final Class<?> cls = Classes.forNameByThread(clsnm);
				checkClass(cls);
				_resolver = cls;
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm, ex);
			}
		} else {
			_resolver = new ExValue(clsnm, String.class);
		}
	}
	/** Constructs with a class name.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 */
	public VariableResolverInfo(String clsnm) throws ClassNotFoundException {
		this(clsnm, null);
	}
	/** Constructs with an initiator that will be reuse each time
	 * {@link #newVariableResolver} is called.
	 */
	public VariableResolverInfo(VariableResolver resolver) {
		super(null);

		if (resolver == null)
			throw new IllegalArgumentException("null");
		_resolver = resolver;
	}

	/** Creates and returns the variable resolver for the specified page.
	 */
	public VariableResolver newVariableResolver(PageDefinition pgdef, Page page)
	throws Exception {
		return newVariableResolver(pgdef.getEvaluator(), page);
	}
		
	/** Creates and returns the variable resolver for the specified page.
	 * @since 3.6.2
	 */
	public VariableResolver newVariableResolver(Evaluator eval, Page page)
	throws Exception {
		if (_resolver instanceof VariableResolver)
			return (VariableResolver)_resolver;

		final Class<?> cls;
		if (_resolver instanceof ExValue) {
			final String clsnm = (String)((ExValue)_resolver)
				.getValue(eval, page);
			if (clsnm == null || clsnm.length() == 0) {
//				if (log.debugable()) log.debug("Ingore "+_resolver+" due to empty");
				return null; //ignore it!!
			}

			try {
				cls = Classes.forNameByThread(clsnm);
				checkClass(cls);
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm+" ("+_resolver+")", ex);
			}
		} else {
			cls = (Class<?>)_resolver;
		}

		return (VariableResolver)newInstance(cls, eval, page);
	}

	//Object//
	public String toString() {
		return "[variable-resolver " + _resolver + "]";
	}
}
