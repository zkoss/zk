/* CDIUtil.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 29, 2009 6:22:32 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.lang.Library;
import org.zkoss.xel.XelException;
/**
 * Some generic utility for use with CDI.
 * <p>Applicable to CDI version 1.0 or later</p>
 * <p> To specify a different JNDI name for the bean manager, please specify them
 * in zk.xml as follows. (By default is <tt>java:comp/env/BeanManager</tt>) since
 * 5.0.11.
 * <pre><code>
 *	<library-property>
 *		<name>org.zkoss.zkplus.cdi.beanManager.jndiName</name>
 *		<value>java:comp/env/BeanManager</value>
 *	</library-property>
 * </code></pre>
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
			_manager = (BeanManager) initialContext.lookup(Library.getProperty(
					"org.zkoss.zkplus.cdi.beanManager.jndiName",
					"java:comp/env/BeanManager"));
		} catch (NamingException e) { // Error getting the home interface
			throw XelException.Aide.wrap(e,
					"Cannot locate the BeanManager for JavaEE 6.");
		}
		return _manager;
	}
}
