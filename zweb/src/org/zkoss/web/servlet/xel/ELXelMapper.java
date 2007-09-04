/* ELXelMapper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 14:48:36     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.util.MethodFunction;

/**
 * A XEL function mapper that is based on an EL function mapper.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ELXelMapper implements FunctionMapper {
	private final javax.servlet.jsp.el.FunctionMapper _mapper;
	/**
	 * @param mapper the EL function mapper. Ignore if null.
	 */
	public ELXelMapper(javax.servlet.jsp.el.FunctionMapper mapper) {
		_mapper = mapper;
	}

	//FunctionMapper//
	/** Note: always returns an empty collection, no matter any function
	 * is defined or not.
	 */
	public Collection getFunctionNames() {
		return Collections.EMPTY_LIST;
	}
	public Function resolveFunction(String prefix, String name) {
		if (_mapper != null) {
			final Method mtd = _mapper.resolveFunction(prefix, name);
			if (mtd != null)
				return new MethodFunction(mtd);
		}
		return null;
	}
	/** Note: always returns an empty collection, no matter any class
	 * is defined or not.
	 */
	public Collection getClassNames() {
		return Collections.EMPTY_LIST;
	}
	public Class resolveClass(String name) {
		return null;
	}
}
