/* DualFunctionMapper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep  1 12:15:16     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.util;

import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.Function;

/**
 * Combine two function mappers into one function mapper.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class DualFunctionMapper implements FunctionMapper {
	private FunctionMapper _frist, _second;

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
		_frist = first;
		_second = second;
	}

	//-- FunctionMapper --//
	public Function resolveFunction(String prefix, String name) {
		Function m = _frist.resolveFunction(prefix, name);
		return m != null ? m: _second.resolveFunction(prefix, name);
	}
}