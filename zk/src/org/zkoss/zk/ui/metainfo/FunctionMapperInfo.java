/* FunctionMapperInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May  5 14:41:03     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Collection;

import org.zkoss.lang.Classes;
import org.zkoss.xel.FunctionMapper;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.Evaluator;

/**
 * A definition of the function mapper ({@link FunctionMapper}).
 *
 * <p>Note: we resolve the class by use fo Classes.forNameByThread.
 * In other words, it doesn't support the class defined in zscript.
 * Why not? Since there is no way to run zscript before the function-mapper
 * directive (and better performance).
 * </p>
 * 
 * @author tomyeh
 * @since 3.5.0
 */
public class FunctionMapperInfo {
	/** A class, an ExValue or an FunctionMapper. */
	private final Object _mapper;
	/** The arguments, never null (might with zero length). */
	private final ExValue[] _args;

	/** Constructs with a class.
	 */
	public FunctionMapperInfo(Class cls, Collection args) {
		checkClass(cls);
		_mapper = cls;
		_args = toExValues(args);
	}
	/** Constructs with a class.
	 */
	public FunctionMapperInfo(Class cls) {
		this(cls, null);
	}
	private static void checkClass(Class cls) {
		if (!FunctionMapper.class.isAssignableFrom(cls))
			throw new UiException(FunctionMapper.class+" must be implemented: "+cls);
	}

	/** Constructs with a class name.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 */
	public FunctionMapperInfo(String clsnm, Collection args)
	throws ClassNotFoundException {
		if (clsnm == null || clsnm.length() == 0)
			throw new IllegalArgumentException("empty");

		if (clsnm.indexOf("${") < 0) {
			try {
				final Class cls = Classes.forNameByThread(clsnm);
				checkClass(cls);
				_mapper = cls;
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm, ex);
			}
		} else {
			_mapper = new ExValue(clsnm, String.class);
		}
		_args = toExValues(args);
	}
	/** Constructs with a class name.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 */
	public FunctionMapperInfo(String clsnm) throws ClassNotFoundException {
		this(clsnm, null);
	}
	/** Constructs with an initiator that will be reuse each time
	 * {@link #newFunctionMapper} is called.
	 */
	public FunctionMapperInfo(FunctionMapper mapper) {
		if (mapper == null)
			throw new IllegalArgumentException("null");
		_mapper = mapper;
		_args = new ExValue[0];
	}
	private static ExValue[] toExValues(Collection args) {
		if (args == null || args.isEmpty())
			return new ExValue[0];

		final ExValue[] evals = new ExValue[args.size()];
		int j = 0;
		for (Iterator it = args.iterator(); it.hasNext();)
			evals[j++] = new ExValue((String)it.next(), Object.class);
		return evals;
	}

	/** Creates and returns the function mapper for the specified pagedefinition and page.
	 */
	public FunctionMapper newFunctionMapper(PageDefinition pgdef, Page page)
	throws Exception {
		return newFunctionMapper(pgdef.getEvaluator(), page);
	}
	
	/** Creates and returns the function mapper for the specified evaluator and page.
	 * @since 3.6.2
	 */
	public FunctionMapper newFunctionMapper(Evaluator eval, Page page)
	throws Exception {
		if (_mapper instanceof FunctionMapper)
			return (FunctionMapper)_mapper;

		final Class cls;
		if (_mapper instanceof ExValue) {
			final String clsnm = (String)((ExValue)_mapper).getValue(eval, page);
			if (clsnm == null || clsnm.length() == 0) {
				return null; //ignore it!!
			}

			try {
				cls = Classes.forNameByThread(clsnm);
				checkClass(cls);
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm+" ("+_mapper+")", ex);
			}
		} else {
			cls = (Class)_mapper;
		}

		return (FunctionMapper)(_args.length == 0 ? cls.newInstance():
			Classes.newInstance(cls, resolveArguments(eval, page)));
	}
	/** Returns the arguments array (by evaluating EL if necessary).
	 */
	private Object[] resolveArguments(Evaluator eval, Page page) {
		final Object[] args = new Object[_args.length];
		for (int j = 0; j < args.length; ++j) //eval order is important
			args[j] = _args[j].getValue(eval, page);
		return args;
	}

	//Object//
	public String toString() {
		return "[function-mapper " + _mapper + "]";
	}
}
