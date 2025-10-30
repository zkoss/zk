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
import javax.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.zkoss.lang.Library;
import org.zkoss.xel.XelException;

/**
 * Some generic utility for use with CDI.
 * <p>Applicable to CDI version 1.1 or later</p>
 * <p> To specify a different JNDI name for the bean manager, please specify them
 * in zk.xml as follows. (By default is <tt>CDI.current().getBeanManager()</tt>) since
 * 10.2.0.
 * <pre>{@code
 *	<library-property>
 *		<name>org.zkoss.zkplus.cdi.beanManager.jndiName</name>
 *		<value>java:comp/env/BeanManager</value>
 *	</library-property>
 * }</pre>
 * @author henrichen
 *
 */
public class CDIUtil {
	private static BeanManager _manager;

	/** Returns the CDI BeanManager.
	 */
	public static BeanManager getBeanManager() {
		if (_manager != null)
			return _manager;

		try {
			final String property = Library.getProperty(
					"org.zkoss.zkplus.cdi.beanManager.jndiName");
			if (property != null) {
				final InitialContext initialContext = new InitialContext();
				_manager = (BeanManager) initialContext.lookup(property);
			} else {
				_manager = CDI.current().getBeanManager();
			}
		} catch (NamingException e) { // Error getting the home interface
			throw XelException.Aide.wrap(e, "Cannot locate the BeanManager for JavaEE.");
		}
		return _manager;
	}
}
