/* XelELMapper.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 20 11:10:01     2011, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.xel.zel;

import java.lang.reflect.Method;

import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;

/**
 * An ZEL function mapper that is based on a XEL function mapper.
 *
 * @author henrichen
 * @since 5.5.0
 */
public class XelELMapper extends org.zkoss.zel.FunctionMapper {
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
