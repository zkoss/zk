/* FuncMethod.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep  1 12:13:49     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import org.zkoss.xel.Function;

/** To provide backward compatibility for deprecated methods
 * @deprecated As of release 3.0.0, only for backward compatibility
 * @author tomyeh
 */
/*package*/ class FuncMethod implements org.zkoss.zk.scripting.Method {
	private final Function _func;
	/*package*/ FuncMethod(Function func) {
		_func = func;
	}
	public Class[] getParameterTypes() {
		return _func.getParameterTypes();
	}
	public Class getReturnType() {
		return _func.getReturnType();
	}
	public Object invoke(Object[] args) throws Exception {
		return _func.invoke(null, args);
	}
}
