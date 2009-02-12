/* XelELMapper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 16:01:10     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.el;

import java.lang.reflect.Method;

import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;

/**
 * An EL function mapper that is based on a XEL function mapper.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class XelELMapper implements javax.servlet.jsp.el.FunctionMapper {
	private FunctionMapper _mapper;

	public XelELMapper(FunctionMapper mapper) {
		_mapper = mapper;
	}
	public Method resolveFunction(String prefix, String name) {
		if (_mapper != null) {
			final Function f = _mapper.resolveFunction(prefix, name);
			if (f != null)
				return f.toMethod();
		}
		return null;
	}
}
