/* ContextClassLoaderFactory.java

	Purpose:
		
	Description:
		
	History:
		12:35 PM 2/15/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.lang;

/**
 * An interface to provide a pluggable context class loader, which is used for
 * {@link Classes#getContextClassLoader(Class)}
 * @author jumperchen
 * @since 8.0.2
 */
public interface ContextClassLoaderFactory {

	/**
	 * Returns the context ClassLoader for the reference class.
	 * @param reference the reference class where it is invoked from.
	 */
	public ClassLoader getContextClassLoader(Class<?> reference);
}
