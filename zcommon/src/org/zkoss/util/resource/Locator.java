/* Locator.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	90/12/07 10:07:23, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import java.net.URL;
import java.io.InputStream;

/**
 * A locator that is able to locate a resource.
 *
 * @author tomyeh
 * @see Locators#getDefault
 */
public interface Locator {
	/** Returns the directory used to resolve the relative path, or null
	 * if relative path is not allowed.
	 *
	 * <p>Note: if the returned directory is not null, it must end with '/'.
	 */
	public String getDirectory();
	/**
	 * Finds the resource with the given name. A resource is some data
	 * (images, audio, text, etc) that can be accessed by class code in
	 * a way that is independent of the location of the code.
	 *
	 * @return a URL for reading the resource, or null if the resource
	 * could not be found or the caller doesn't have adequate privileges
	 * to get the resource
	 */
	public URL getResource(String name);
	/**
	 * Returns an input stream for reading the specified resource.
	 *
	 * @return an input stream for reading the resource, or null if the
	 * resource could not be found or the caller doesn't have adequate
	 * privileges to get the resource
	 */
	public InputStream getResourceAsStream(String name);
}
