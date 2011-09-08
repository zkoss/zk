/* ClassResolver.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 27 11:54:51 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.lang;

/**
 * A resolver that can resolve the class of the given name.
 * @author tomyeh
 * @since 5.5.0
 */
public interface ClassResolver {
	/** Resolves the class of the specified name.
	 * @exception ClassNotFoundException if the class is not found.
	 */
	public Class<?> resolveClass(String clsnm) throws ClassNotFoundException;
}
