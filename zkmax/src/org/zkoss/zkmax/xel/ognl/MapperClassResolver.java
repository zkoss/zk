/* MapperClassResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 17 11:40:12     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.ognl;

import java.util.Map;

import ognl.ClassResolver;

import org.zkoss.xel.FunctionMapper;

/**
 * A class resolver based on {@link FunctionMapper}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
/*package*/ class MapperClassResolver implements ClassResolver {
	private final FunctionMapper _mapper;
	public MapperClassResolver(FunctionMapper mapper) {
		_mapper = mapper;
	}

	public Class classForName(String className, Map context)
	throws ClassNotFoundException {
		final Class cls =
			_mapper != null ? _mapper.resolveClass(className): null;
		if (cls == null)
			throw new ClassNotFoundException(className);
		return cls;
	}
}
