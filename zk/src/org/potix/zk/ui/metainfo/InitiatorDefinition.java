/* InitiatorDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Mar 31 14:24:37     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import java.util.List;

import com.potix.lang.Classes;
import com.potix.util.logging.Log;

import com.potix.zk.ui.Page;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Initiator;

/**
 * A definition of the initiator.
 *
 * <p>Note: we resolve the class by use fo Classes.forNameByThread.
 * In other words, it doesn't support the class defined in zscript.
 * Why not? Since there is no way to run zscript before the init directive
 * (and better performance).
 * </p>
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class InitiatorDefinition {
	private static final Log log = Log.lookup(InitiatorDefinition.class);

	/** A class, a EL string or an Initiator. */
	private final Object _init;
	/** The arguments, never null (might with zero length). */
	private final String[] _args;

	/** Constructs with a class, and {@link #newInitiator} will
	 * instantiate a new instance.
	 */
	public InitiatorDefinition(Class cls, String[] args) {
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
	public InitiatorDefinition(Class cls, List args) {
		this(cls, args != null ?
			(String[])args.toArray(new String[args.size()]): null);
	}
	/** Constructs with a class name and {@link #newInitiator} will
	 * instantiate a new instance.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 */
	public InitiatorDefinition(String clsnm, String[] args)
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
	public InitiatorDefinition(String clsnm, List args)
	throws ClassNotFoundException {
		this(clsnm, args != null ?
			(String[])args.toArray(new String[args.size()]): null);
	}
	/** Constructs with an initiator that will be reuse each time
	 * {@link #newInitiator} is called.
	 */
	public InitiatorDefinition(Initiator init, String[] args) {
		if (init == null)
			throw new IllegalArgumentException("null");
		_init = init;
		_args = args != null ? args: new String[0];
	}
	/** Constructs with an initiator that will be reuse each time
	 * {@link #newInitiator} is called.
	 */
	public InitiatorDefinition(Initiator init, List args) {
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
