/* PageNameSpace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun  6 15:30:39     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl.bsh;

import java.util.List;
import java.util.Iterator;

import bsh.BshClassManager;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.UtilEvalError;

import com.potix.zk.ui.util.VariableResolver;

/**
 * The name space used as the interpter's top-level name space (i.e.,
 * page-level).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class PageNameSpace extends NameSpace {
	private final BshInterpreter _ip;

	/** Don't call this method. */
    /*package*/ PageNameSpace(BshInterpreter ip, BshClassManager classManager, String name) {
    	super(classManager, name);
    	_ip = ip;
    }

	//-- extra --//
	/** Resolves the specified variable name from
	 * {@link BshInterpreter#getVariableResolvers}.
	 */
	/*package*/ Object resolve(String name) {
		if (!"bsh".equals(name)) {
			final List resolvers = _ip.getVariableResolvers();
			if (resolvers != null) {
				for (Iterator it = resolvers.iterator(); it.hasNext();) {
					final Object v =
						((VariableResolver)it.next()).getVariable(name);
					if (v != null) return v;
				}
			}
		}
		return Primitive.VOID;
	}

	//-- super --//
	public Object getVariable( String name, boolean recurse ) 
	throws UtilEvalError {
		final Object o = super.getVariable(name, recurse);
		return Primitive.unwrap(o) == null ? resolve(name): o;
	}
}
