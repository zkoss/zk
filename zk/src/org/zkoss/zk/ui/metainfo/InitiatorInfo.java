/* InitiatorInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Mar 31 14:24:37     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.zkoss.lang.Classes;
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
public class InitiatorInfo {
//	private static final Log log = Log.lookup(InitiatorInfo.class);

	/** A class, an ExValue or an Initiator. */
	private final Object _init;
	/** The arguments, never null (might with zero length). */
	private final Map _args;

	/** Constructs with a class, and {@link #newInitiator} will
	 * instantiate a new instance.
	 * @since 3.6.2
	 */
	public InitiatorInfo(Class cls, Map args) {
		checkClass(cls);
		_init = cls;
		_args = toExValues(args);
	}
	/** Constructs with a class name and {@link #newInitiator} will
	 * instantiate a new instance.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 * @since 3.6.2
	 */
	public InitiatorInfo(String clsnm, Map args)
	throws ClassNotFoundException {
		_init = toClass(clsnm);
		_args = toExValues(args);
	}
	/** Constructs with an initiator that will be reuse each time
	 * {@link #newInitiator} is called.
	 * @since 3.6.2
	 */
	public InitiatorInfo(Initiator init, Map args) {
		if (init == null)
			throw new IllegalArgumentException("null");
		_init = init;
		_args = toExValues(args);
	}

	private static Map toExValues(Map args) {
		if (args == null || args.isEmpty())
			return null;

		for (Iterator it = args.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			me.setValue(new ExValue((String)me.getValue(), Object.class));
		}
		return args;
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

	/** Creaetes and returns the initiator.
	 */
	public Initiator newInitiator(PageDefinition pgdef, Page page)
	throws Exception {
		if (_init instanceof Initiator)
			return (Initiator)_init;

		final Class cls;
		if (_init instanceof ExValue) {
			final String clsnm = (String)
				((ExValue)_init).getValue(pgdef.getEvaluator(), page);
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
		return (Initiator)cls.newInstance();
	}
	/** Returns the arguments array (and evaluates EL if necessary).
	 * @since 3.6.2
	 */
	public Map resolveArguments(PageDefinition pgdef, Page page) {
		if (_args == null)
			return Collections.EMPTY_MAP;

		final Evaluator eval = pgdef.getEvaluator();
		final Map args = new LinkedHashMap(); //eval order is important
		for (Iterator it = _args.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			args.put(me.getKey(),
				((ExValue)me.getValue()).getValue(eval, page));
		}
		return args;
	}
}
