/* BSHMethod.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jan 25 14:13:43     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsh;

import org.zkoss.zk.scripting.Interpreter;

/**
 * An implementation of {@link org.zkoss.zk.scripting.Method} on top of BeanShell.
 * 
 * @author tomyeh
 */
public class BSHMethod
implements org.zkoss.zk.scripting.Method, java.io.Serializable {
	private final bsh.BshMethod _method;
	public BSHMethod(bsh.BshMethod method) {
		if (method == null)
			throw new IllegalArgumentException("null");
		_method = method;
	}

	//-- Method --//
	public Class[] getParameterTypes() {
		return _method.getParameterTypes();
	}
	public Class getReturnType() {
		return _method.getReturnType();
	}
	public Object invoke(Interpreter ip, Object[] args) throws Exception {
		return _method.invoke(args != null ? args: new Object[0],
			((BSHInterpreter)ip).getNativeInterpreter());
	}
}
