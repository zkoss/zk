/* RequestContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 12:32:50     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import java.io.Writer;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.zkoss.xel.VariableResolver;

/**
 * The context used to simplify the signatures of XEL function.
 *
 * <p>It is designed to make the signature of XEL functions
 * (see {@link org.zkoss.web.fn.ServletFns}) simpler.
 * For example, {@link org.zkoss.web.fn.ServletFns#isExplorer} requires
 * no argument, since it assumes the current context can be retrieved
 * from {@link RequestContexts#getCurrent}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface RequestContext {
	/** Returns the writer, never null.
	 */
	public Writer getOut() throws IOException;
	/** Returns the request, or null if not available.
	 */
	public ServletRequest getRequest();
	/** Returns the response, or null if not available.
	 */
	public ServletResponse getResponse();
	/** Returns the request, or null if not available.
	 */
	public ServletContext getServletContext();
	/** Returns the variable resolver.
	 */
	public VariableResolver getVariableResolver();
}
