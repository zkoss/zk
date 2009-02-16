/* Extendlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  4 15:43:39     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A plugin of {@link ClassWebResource} to process particular
 * content.
 *
 * <p>To add a resource processor to {@link ClassWebResource}, use
 * {@link ClassWebResource#addExtendlet}.
 *
 * @author tomyeh
 * @since 2.4.1
 * @see ClassWebResource#addExtendlet
 */
public interface Extendlet {
	/** Used with {@link #getFeature} to indicate the extendlet can
	 * be included directly without going thru Web container.
	 *
	 * <p>In other words, if getFeature(ALLOW_DIRECT_INCLUDE) is true
	 * and a corresponding page is included, then {@link #service}
	 * will be invoked directly (rather than calling RequestDispatcher's
	 * include method.
	 *
	 * @since 3.5.2
	 */
	public static final int ALLOW_DIRECT_INCLUDE = 0x0001;

	/** Initializes the resouorce processor.
	 */
	public void init(ExtendletConfig config);
	/** Returns if the specified feature is supported.
	 * @since 3.5.2
	 */
	public boolean getFeature(int feature);

	/** Process the specified request.
	 *
	 * @param path the path mapped to this resource processor.
	 * @param extra an additional string to output (to response.getWriter).
	 * Ignored if null.
	 */
	public void service(HttpServletRequest request,
	HttpServletResponse response, String path, String extra)
	throws ServletException, IOException;
}
