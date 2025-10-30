/* F327Resolver.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 26 15:26:17 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zktest.test2;

import org.zkoss.xel.*;

/**
 * Used for testing Feature ZK-327.
 * 
 * @author tomyeh
 */
public class F327Resolver implements VariableResolver {
	public Object resolveVariable(String name) {
		return "resolver".equals(name) ? this: null;
	}
}
