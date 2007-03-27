/* InitiatorInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Mar 31 14:24:37     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.List;

import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Initiator;

/**
 * A initiator node on the ZUML page.
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
	private static final Log log = Log.lookup(InitiatorInfo.class);

	/** A class, a EL string or an Initiator. */
	private final Object _init;
	/** The arguments, never null (might with zero length). */
	private final String[] _args;

	/** Constructs with a class, and {@link #newInitiator} will
	 * instantiate a new instance.
	 */
	public InitiatorInfo(Class cls, String[] args) {
		checkClass(cls);
		_init = cls;
		_args = args != null ? args: new String[0];
	}
	private static void checkClass(Class cls) {
		if (!Initiator.class.isAssignableFrom(cls))
			throw new UiException(Initiator.class+" must be implemented: "+cls);
	}

	/** Constructs with a class, and {@link #newInitiator} will
	 * instantiate a new instance.
	 */
	public InitiatorInfo(Class cls, List args) {
		this(cls, args != null ?
			(String[])args.toArray(new String[args.size()]): null);
	}
	/** Constructs with a class name and {@link #newInitiator} will
	 * instantiate a new instance.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 */
	public InitiatorInfo(String clsnm, String[] args)
	throws ClassNotFoundException {
		if (clsnm == null || clsnm.length() == 0)
			throw new IllegalArgumentException("empty");

		if (clsnm.indexOf("${") < 0) {
			try {
				final Class cls = Classes.forNameByThread(clsnm);
				checkClass(cls);
				_init = cls;
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm, ex);
			}
		} else {
			_init = clsnm;
		}
		_args = args != null ? args: new String[0];
	}
	/** Constructs with a class name and {@link #newInitiator} will
	 * instantiate a new instance.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 */
	public InitiatorInfo(String clsnm, List args)
	throws ClassNotFoundException {
		this(clsnm, args != null ?
			(String[])args.toArray(new String[args.size()]): null);
	}
	/** Constructs with an initiator that will be reuse each time
	 * {@link #newInitiator} is called.
	 */
	public InitiatorInfo(Initiator init, String[] args) {
		if (init == null)
			throw new IllegalArgumentException("null");
		_init = init;
		_args = args != null ? args: new String[0];
	}
	/** Constructs with an initiator that will be reuse each time
	 * {@link #newInitiator} is called.
	 */
	public InitiatorInfo(Initiator init, List args) {
		this(init, args != null ?
			(String[])args.toArray(new String[args.size()]): null);
	}

	/** Creaetes and returns the initiator.
	 */
	public Initiator newInitiator(Page page) throws Exception {
		if (_init instanceof Initiator)
			return (Initiator)_init;

		final Class cls;
		if (_init instanceof String) {
			final String clsnm = (String)Executions.evaluate(
				page, (String)_init, String.class);
			if (clsnm == null || clsnm.length() == 0) {
				if (log.debugable()) log.debug("Ingore "+_init+" due to empty");
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
	/** Returns the arguments array (by evaluating EL if necessary).
	 */
	public Object[] getArguments(Page page) {
		final Object[] args = new Object[_args.length];
		for (int j = 0; j < args.length; ++j)
			args[j] = Executions.evaluate(page, _args[j], Object.class);
		return args;
	}
}
