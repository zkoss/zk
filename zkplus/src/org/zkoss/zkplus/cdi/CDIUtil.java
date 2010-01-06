/* CDIUtil.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 29, 2009 6:22:32 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.xel.XelException;
/**
 * Some generic utility for use with CDI.
 * @author henrichen
 *
 */
public class CDIUtil {
	private static BeanManager _manager;
	/** Returns the CDI BeanManager. 
	 * Default implementation use JNDI to lookup "java:comp/env/BeanManager". 
	 */ 
	public static BeanManager getBeanManager() {
		if (_manager != null)
			return _manager;
			
		try {
			final InitialContext initialContext = new InitialContext(); 
		   _manager = (BeanManager) initialContext.lookup("java:comp/env/BeanManager");
		} catch (NamingException e) { // Error getting the home interface
		   throw XelException.Aide.wrap(e, "Cannot locate the BeanManager for JavaEE 6.");
		}
		return _manager;
	}
}
