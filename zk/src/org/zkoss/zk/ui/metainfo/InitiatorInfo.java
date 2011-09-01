/* InitiatorInfo.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 31 14:24:37     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.zkoss.lang.Classes;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.Evaluator;

/**
 * The init directive in the ZUML page.
 *
 * <p>Note: we resolve the class by use fo Classes.forNameByThread.
 * In other words, it doesn't support the class defined in zscript.
 * Why not? Since there is no way to run zscript before the init directive
 * (and better performance).
 * </p>
 *
 * @author tomyeh
 */
public class InitiatorInfo extends ArgumentInfo {
//	private static final Log log = Log.lookup(InitiatorInfo.class);

	/** A class, an ExValue or an Initiator. */
	private final Object _init;

	/** Constructs with a class, and {@link #newInitiator} will
	 * instantiate a new instance.
	 * @since 3.6.2
	 */
	public InitiatorInfo(Class cls, Map args) {
		super(args);
		checkClass(cls);
		_init = cls;
	}
	/** Constructs with a class name and {@link #newInitiator} will
	 * instantiate a new instance.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 * @since 3.6.2
	 */
	public InitiatorInfo(String clsnm, Map args)
	throws ClassNotFoundException {
		super(args);
		_init = toClass(clsnm);
	}
	/** Constructs with an initiator that will be reuse each time
	 * {@link #newInitiator} is called.
	 * @since 3.6.2
	 */
	public InitiatorInfo(Initiator init, Map args) {
		super(args);
		if (init == null)
			throw new IllegalArgumentException("null");
		_init = init;
	}

	private static Object toClass(String clsnm) throws ClassNotFoundException {
		if (clsnm == null || clsnm.length() == 0)
			throw new IllegalArgumentException();

		if (clsnm.indexOf("${") < 0) {
			try {
				final Class cls = Classes.forNameByThread(clsnm);
				checkClass(cls);
				return cls;
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm, ex);
			}
		} else {
			return new ExValue(clsnm, String.class);
		}
	}
	private static void checkClass(Class cls) {
		if (!Initiator.class.isAssignableFrom(cls))
			throw new UiException(Initiator.class+" must be implemented: "+cls);
	}

	/** Creates and returns the initiator, or null if no initiator is resolved.
	 * Notice that {@link Initiator#doInit} was called before returned.
	 */
	public Initiator newInitiator(PageDefinition pgdef, Page page)
	throws Exception {
		return newInitiator(pgdef.getEvaluator(), page);
	}
	/** Creates and returns the initiator, or null if no initiator is resolved.
	 * Notice that {@link Initiator#doInit} was called before returned.
	 * @since 3.6.2
	 */
	public Initiator newInitiator(Evaluator eval, Page page)
	throws Exception {
		if (_init instanceof Initiator)
			return doInit((Initiator)_init, eval, page);

		final Class cls;
		if (_init instanceof ExValue) {
			final String clsnm = (String)
				((ExValue)_init).getValue(eval, page);
			if (clsnm == null || clsnm.length() == 0) {
//				if (log.debugable()) log.debug("Ingore "+_init+" due to empty");
				return null; //ignore it!!
			}

			try {
				cls = Classes.forNameByThread(clsnm);
				checkClass(cls);
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm+" ("+_init+")", ex);
			}
		} else {
			cls = (Class)_init;
		}
		return doInit((Initiator)cls.newInstance(), eval, page);
	}
	private Initiator doInit(Initiator init, Evaluator eval, Page page)
	throws Exception {
		final Map args = resolveArguments(eval, page);
		try {
			init.doInit(page, args);
		} catch (AbstractMethodError ex) { //backward compatible prior to 3.6.2
			final Method m = init.getClass().getMethod(
				"doInit", new Class[] {Page.class, Object[].class});
			Fields.setAccessible(m, true);
			m.invoke(init, new Object[] {page, toArray(args)});
		}
		return init;
	}
}
