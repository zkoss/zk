/* FuncMapper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May  5 15:57:33     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.xel.Function;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.MethodFunction;
import org.zkoss.xel.util.SimpleMapper;

/**
 * Used to test with test2/Z31-funcmap.zul
 *
 * @author tomyeh
 */
public class FuncMapper extends SimpleMapper {
	public Function resolveFunction(String prefix, String name)
	throws XelException {
		if ("quote".equals(name)) {
			try {
				return new MethodFunction(
					FuncMapper.class.getMethod("quote", new Class[] {String.class}));
			} catch (Exception ex) {
				throw XelException.Aide.wrap(ex);
			}
		}
		return null;
	}

	public static String quote(String s){
		return "'" + s +"'";
	}
}
