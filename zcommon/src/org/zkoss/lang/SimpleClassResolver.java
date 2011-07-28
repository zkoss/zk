/* SimpleClassResolver.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 27 13:35:10 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.lang;

/**
 * A simple resolver that depends on {@link Classes#forNameByThread}.
 * @author tomyeh
 * @since 5.1.0
 * @see ImportedClassResolver
 */
public class SimpleClassResolver implements ClassResolver, java.io.Serializable {
	public Class resolveClass(String clsnm) throws ClassNotFoundException {
		return Classes.forNameByThread(clsnm);
	}
}
