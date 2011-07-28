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
 * <p>Note: it is not serializable.</p>
 *
 * @author tomyeh
 */
public class InitiatorInfo extends ArgumentInfo { //directive
//	private static final Log log = Log.lookup(InitiatorInfo.class);

	/** A class, an ExValue or an Initiator. */
	private Object _init;

	/** Constructs with a class, and {@link #newInitiator} will
	 * instantiate a new instance.
	 * @param args the map of arguments. Ignored if null.<br/>
	 * Notice that, once assigned, the map belongs to this object, and the caller
	 * shall not access it again
	 * @since 3.6.2
	 */
	public InitiatorInfo(Class<? extends Initiator> cls, Map<String, String> args) {
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
	public InitiatorInfo(String clsnm, Map<String, String> args)
	throws ClassNotFoundException {
		super(args);

		if (clsnm == null || clsnm.length() == 0)
			throw new IllegalArgumentException();

		if (clsnm.indexOf("${") < 0) {
			if (clsnm.indexOf('.') >= 0) { //resolve it now
				try {
					final Class cls = Classes.forNameByThread(clsnm);
					checkClass(cls);
					_init = cls;
				} catch (ClassNotFoundException ex) {
					throw new ClassNotFoundException("Class not found: "+clsnm, ex);
				}
			} else { //it might depend on <?import?>
				_init = clsnm;
			}
		} else {
			_init = new ExValue(clsnm, String.class);
		}
	}
	/** Constructs with an initiator that will be reuse each time
	 * {@link #newInitiator} is called.
	 * @since 3.6.2
	 */
	public InitiatorInfo(Initiator init, Map<String, String> args) {
		super(args);
		if (init == null)
			throw new IllegalArgumentException("null");
		_init = init;
	}

	private static void checkClass(Class<?> cls) {
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

		String clsnm = null;
		if (_init instanceof ExValue) {
			clsnm = (String)((ExValue)_init).getValue(eval, page);
			if (clsnm == null || clsnm.length() == 0) {
//				if (log.debugable()) log.debug("Ingore "+_init+" due to empty");
				return null; //ignore it!!
			}
		} else if (_init instanceof String) {
			clsnm = (String)_init;
		}

		final Class<?> cls;
		if (clsnm != null) {
			try {
				cls = page != null ?
					page.resolveClass(clsnm): Classes.forNameByThread(clsnm);
				checkClass(cls);
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm+" ("+_init+")", ex);
			}
			if (clsnm.equals(_init))
				_init = cls; //cache it for better performance
		} else {
			cls = (Class<?>)_init;
		}
		return doInit((Initiator)cls.newInstance(), eval, page);
	}
	private Initiator doInit(Initiator init, Evaluator eval, Page page)
	throws Exception {
		final Map<String, Object> args = resolveArguments(eval, page);
		init.doInit(page, args);
		return init;
	}
}
