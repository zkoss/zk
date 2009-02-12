/* URIInterceptor.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 17:05:53     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

/**
 * Used to intercept the loading of ZUML pages associated with the specfied URI.
 * Developers usually use it to do the security check.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * Then, an instance of the specified class is created.
 * </li>
 * <li>Each time ZK wants to load a page definition based on an URI,
 * {@link #request} is called against the instance created in
 * the previous step.</li>
 * </ol>
 *
 * <p>Note:
 * <ul>
 * <li>Unlike {@link ExecutionInit} and others listeners, the same instance of
 * {@link URIInterceptor} is used for the whole application.
 * Thus, you have to make sure it can be accessed concurrently.</li>
 * <li>{@link #request} is called even if the page definition is cached.</li>
 * </ul>
 * 
 * <h3>Differences to {@link RequestInterceptor}</h3>
 *
 * <p>{@link URIInterceptor} is called when retrieving a page definition
 * from an URI ({@link org.zkoss.zk.ui.metainfo.PageDefinitions#getPageDefinition}). It may or may not be caused by a client request.
 * On the other hand, {@link RequestInterceptor} is called
 * when ZK Loader or ZK Update Engine is receiving a client request.
 *
 * @author tomyeh
 * @see RequestInterceptor
 */
public interface URIInterceptor {
	/** Called when the current user requests to load
	 * the page of the specified URI.
	 *
	 * <p>To deny the access, the class shall throw an excetion.
	 */
	public void request(String uri) throws Exception;
}
