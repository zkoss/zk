/* DualFunctionMapper.java

	Purpose:
		
	Description:
		
	History:
		Sat Sep  1 12:15:16     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.util;

import java.util.Collection;

import org.zkoss.util.DualCollection;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.Function;

/**
 * Combine two function mappers into one function mapper.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class DualFunctionMapper implements FunctionMapper, java.io.Serializable {
	private FunctionMapper _first, _second;

	/** Returns a function mapper by combining two function mappers.
	 * It checks whether any of them is null, or equals. And, returns
	 * the non-null one if another is null.
	 * If both null, it returns null.
	 */
	public static final
	FunctionMapper combine(FunctionMapper first, FunctionMapper second) {
		if (first == second) //we don't use equals to have better performance
			return first;

		if (first != null)
			if (second != null)
				return new DualFunctionMapper(first, second);
			else
				return first;
		else
			return second;
	}
	/** Constructor.
	 * It is better to use {@link #combine} instead of this method
	 * since it checks whether any of them is null or equals.
	 */
	public DualFunctionMapper(FunctionMapper first, FunctionMapper second) {
		_first = first;
		_second = second;
	}

	//-- FunctionMapper --//
	public Function resolveFunction(String prefix, String name) {
		Function m = _first != null ? _first.resolveFunction(prefix, name): null;
		return m != null ? m:
			_second != null ? _second.resolveFunction(prefix, name): null;
	}
	public Collection getClassNames() {
		return combine(
			_first != null ? _first.getClassNames(): null,
			_second != null ? _second.getClassNames(): null);
	}
	public Class resolveClass(String name) {
		Class m = _first != null ? _first.resolveClass(name): null;
		return m != null ? m:
			_second != null ? _second.resolveClass(name): null;
	}
	private static Collection combine(Collection first, Collection second) {
		return DualCollection.combine(
			first != null && !first.isEmpty() ? first: null,
			second != null && !second.isEmpty() ? second: null);
	}
}