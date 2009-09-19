/* DspContext.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:42:05     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp;

import java.util.Map;
import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletException;

import org.zkoss.util.resource.Locator;
import org.zkoss.web.servlet.xel.RequestContext;

/**
 * The context used with {@link Interpreter#interpret}.
 *
 * @author tomyeh
 * @see Interpreter
 */
public interface DspContext extends RequestContext {
	/** Returns the locator for loading resources, such as taglib.
	 * You might return null if the page not referencing external resources.
	 *
	 * <p>To load the resource from a web application, use
	 * {@link org.zkoss.web.util.resource.ServletContextLocator}
	 * To load the resource from class path, use
	 * org.zkoss.util.resource.Resources.getDefault().
	 */
	public Locator getLocator();

	/** Sets the content type of the output.
	 */
	public void setContentType(String ctype);

	/** Returns the encoded URL.
	 * The returned URL is also encoded with HttpServletResponse.encodeURL.
	 *
	 * @param uri it must be empty or starts with "/". It might contain
	 * "*" for current browser code and Locale.
	 * @return the complete URL (excluding the machine name).
	 * It includes the context path and the servelt to interpret
	 * this extended resource.
	 * @since 3.5.2
	 */
	public String encodeURL(String uri)
	throws ServletException, IOException;
	/** Includes the specified URI and render the result to the specified
	 * output.
	 *
	 * @param uri the URI to include. It is OK to relevant (without leading
	 * '/'). If starts with "/", the context path of request is assumed.
	 * To reference to foreign context, use "~ctx/" where ctx is the
	 * context path of the foreign context (without leading '/').
	 * @param params a map of parameters, or null to ignore.
	 * The map is passed thru the request attribute called arg.
	 * @since 3.5.2
	 */
	public void include(String uri, Map params)
	throws ServletException, IOException;
	/** Returns whether this page is included.
	 * @since 3.5.2
	 */
	public boolean isIncluded();

	/** Changes the writer of this context to the specified one.
	 *
	 * @param out the new writer. If null, it is restored to
	 * the default one.
	 */
	public void setOut(Writer out);
}
