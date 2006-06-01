/* VariableResolverDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 18:12:56     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import com.potix.lang.Classes;
import com.potix.util.logging.Log;

import com.potix.zk.ui.Page;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.VariableResolver;

/**
 * A definition of the variable resolver ({@link VariableResolver}).
 *
 * <p>Note: we resolve the class by use fo Classes.forNameByThread.
 * In other words, it doesn't support the class defined in zscript.
 * Why not? Since there is no way to run zscript before the init directive
 * (and better performance).
 * </p>
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class VariableResolverDefinition {
	private static final Log log = Log.lookup(VariableResolverDefinition.class);

	/** A class, a EL string or an VariableResolver. */
	private final Object _resolver;

	/** Constructs with a class, and {@link #newVariableResolver} will
	 * instantiate a new instance.
	 */
	public VariableResolverDefinition(Class cls) {
		checkClass(cls);
		_resolver = cls;
	}
	private static void checkClass(Class cls) {
		if (!VariableResolver.class.isAssignableFrom(cls))
			throw new UiException(VariableResolver.class+" must be implemented: "+cls);
	}

	/** Constructs with a class name and {@link #newVariableResolver} will
	 * instantiate a new instance.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 */
	public VariableResolverDefinition(String clsnm)
	throws ClassNotFoundException {
		if (clsnm == null || clsnm.length() == 0)
			throw new IllegalArgumentException("empty");

		if (clsnm.indexOf("${") < 0) {
			try {
				final Class cls = Classes.forNameByThread(clsnm);
				checkClass(cls);
				_resolver = cls;
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm, ex);
			}
		} else {
			_resolver = clsnm;
		}
	}
	/** Constructs with an initiator that will be reuse each time
	 * {@link #newVariableResolver} is called.
	 */
	public VariableResolverDefinition(VariableResolver resolver) {
		if (resolver == null)
			throw new IllegalArgumentException("null");
		_resolver = resolver;
	}

	/** Creaetes and returns the initiator.
	 */
	public VariableResolver newVariableResolver(PageDefinition pagedef,
	Page page) throws Exception {
		if (_resolver instanceof VariableResolver)
			return (VariableResolver)_resolver;

		final Class cls;
		if (_resolver instanceof String) {
			final String clsnm = (String)Executions.evaluate(
				pagedef, page, (String)_resolver, String.class);
			if (clsnm == null || clsnm.length() == 0) {
				if (log.debugable()) log.debug("Ingore "+_resolver+" due to empty");
				return null; //ignore it!!
			}

			try {
				cls = Classes.forNameByThread(clsnm);
				checkClass(cls);
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm+" ("+_resolver+")", ex);
			}
		} else {
			cls = (Class)_resolver;
		}
		return (VariableResolver)cls.newInstance();
	}
}
