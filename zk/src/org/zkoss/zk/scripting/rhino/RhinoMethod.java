/* RhinoMethod.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb  9 10:55:35     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Function;

import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Method;

/**
 * A implementation of Method with Rhino.
 *
 * @author tomyeh
 */
/*package*/ class RhinoMethod implements Method {
	private final Function _func;
	/*package*/ RhinoMethod(Function func) {
		if (func == null)
			throw new IllegalArgumentException("null");
		_func = func;
	}

	//-- Method --//
	public Class[] getParameterTypes() {
		return new Class[0];
	}
	public Class getReturnType() {
		return Object.class;
	}
	public Object invoke(Interpreter ip, Object[] args) throws Exception {
		final Context ctx = Context.enter();
		try {
			final Scriptable scope = ((RhinoInterpreter)ip).getGlobalScope();
			return _func.call(ctx, scope, scope, args);
		} finally {
			Context.exit();
		}
	}
}
