/* FunctionMapperInfo.java

	Purpose:
		
	Description:
		
	History:
		Mon May  5 14:41:03     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Map;

import org.zkoss.lang.Classes;
import org.zkoss.xel.FunctionMapper;

import org.zkoss.zk.ui.Page;
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
 * <p>Note: it is not serializable.</p>
 * @author tomyeh
 * @since 3.5.0
 */
public class FunctionMapperInfo extends ArgumentInfo { //directive
	/** A class, an ExValue or an FunctionMapper. */
	private Object _mapper;

	/** Constructs with a class.
	 * @param args the map of arguments. Ignored if null.<br/>
	 * Notice that, once assigned, the map belongs to this object, and the caller
	 * shall not access it again
	 * @since 3.6.1
	 */
	public FunctionMapperInfo(Class<? extends FunctionMapper> cls, Map<String, String> args) {
		super(args);
		checkClass(cls);
		_mapper = cls;
	}
	/** Constructs with a class.
	 */
	public FunctionMapperInfo(Class<? extends FunctionMapper> cls) {
		this(cls, null);
	}
	private static void checkClass(Class<?> cls) {
		if (!FunctionMapper.class.isAssignableFrom(cls))
			throw new UiException(FunctionMapper.class+" must be implemented: "+cls);
	}

	/** Constructs with a class name.
	 *
	 * @param clsnm the class name; it could be an EL expression.
	 */
	public FunctionMapperInfo(String clsnm, Map<String, String> args)
	throws ClassNotFoundException {
		super(args);

		if (clsnm == null || clsnm.length() == 0)
			throw new IllegalArgumentException("empty");

		if (clsnm.indexOf("${") < 0) {
			if (clsnm.indexOf('.') >= 0) { //resolve it now
				try {
					final Class<?> cls = Classes.forNameByThread(clsnm);
					checkClass(cls);
					_mapper = cls;
				} catch (ClassNotFoundException ex) {
					throw new ClassNotFoundException("Class not found: "+clsnm, ex);
				}
			} else { //it might depend on <?import?>
				_mapper = clsnm;
			}
		} else {
			_mapper = new ExValue(clsnm, String.class);
		}
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
		super(null);

		if (mapper == null)
			throw new IllegalArgumentException("null");
		_mapper = mapper;
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

		String clsnm = null;
		if (_mapper instanceof ExValue) {
			clsnm = (String)((ExValue)_mapper).getValue(eval, page);
			if (clsnm == null || clsnm.length() == 0) {
				return null; //ignore it!!
			}
		} else if (_mapper instanceof String) {
			clsnm = (String)_mapper;
		}

		final Class<?> cls;
		if (clsnm != null) {
			try {
				cls = page != null ?
					page.resolveClass(clsnm): Classes.forNameByThread(clsnm);
				checkClass(cls);
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Class not found: "+clsnm+" ("+_mapper+")", ex);
			}
			if (clsnm.equals(_mapper))
				_mapper = cls; //cache it for better performance
		} else {
			cls = (Class<?>)_mapper;
		}

		return (FunctionMapper)newInstance(cls, eval, page);
	}

	//Object//
	public String toString() {
		return "[function-mapper " + _mapper + "]";
	}
}
