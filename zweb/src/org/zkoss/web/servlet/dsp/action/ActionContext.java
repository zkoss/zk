/* ActionContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 09:30:59     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action;

import java.io.Writer;
import java.io.IOException;
import java.util.Map;

import org.zkoss.web.servlet.dsp.DspException;

/**
 * Encapsulates a portion of DSP page in an object that can be invoked
 * as many times as needed.
 *
 * @author tomyeh
 */
public interface ActionContext {
	/** The page scope. */
	public static final int PAGE_SCOPE = 0;
	/** The request scope. */
	public static final int REQUEST_SCOPE = 1;
	/** The session scope. */
	public static final int SESSION_SCOPE = 2;
	/** The application scope. */
	public static final int APPLICATION_SCOPE = 3;

	/** Returns the attribute of the specified scope.
	 * @param scope one of {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE},
	 * {@link #SESSION_SCOPE} and {@link #APPLICATION_SCOPE}.
	 */
	public Object getAttribute(String name, int scope);
	/** Sets the attribute of the specified scope.
	 * @param scope one of {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE},
	 * {@link #SESSION_SCOPE} and {@link #APPLICATION_SCOPE}.
	 */
	public void setAttribute(String name, Object value, int scope);
	/** Removes the attribute of the specified scope.
	 * @param scope one of {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE},
	 * {@link #SESSION_SCOPE} and {@link #APPLICATION_SCOPE}.
	 */
	public void removeAttribute(String name, int scope);
	/** Finds the attribute from page, request, session to application scope
	 */
	public Object findAttribute(String name);

	/** Sets the content type.
	 */
	public void setContentType(String ctype);

	/** Returns the current output.
	 */
	public Writer getOut() throws IOException;
	/** Returns the parent action, or null if no parent at all.
	 */
	public Action getParent();

	/** Renders the nested fragment.
	 *
	 * @param out the output. If null, {@link #getOut} is assumed.
	 */
	public void renderFragment(Writer out)
	throws DspException, IOException;
	/** Includes the specified URI and render the result to the specified
	 * output.
	 *
	 * @param uri the URI to include. It is OK to relevant (without leading
	 * '/'). If starts with "/", the context path of request is assumed.
	 * To reference to foreign context, use "~ctx/" where ctx is the
	 * context path of the foreign context (without leading '/').
	 * @param params a map of parameters, or null to ignore.
	 * The map is passed thru the request attribute called arg.
	 */
	public void include(String uri, Map params)
	throws DspException, IOException;

	/** Returns whether this page is included.
	 * @since 3.0.6
	 */
	public boolean isIncluded();

	/** Encodes the specified URI.
	 * <p>In additions, if uri starts with "/", the context path, e.g., /we2,
	 * is prefixed.
	 * In other words, "/ab/cd" means it is relevant to the servlet
	 * context path (say, "/we2").
	 *
	 * <p>If uri starts with "~abc/", it means it is relevant to a foreign
	 * context called /abc. And, it will be converted to "/abc/" first
	 * (without prefix request.getContextPath()).
	 *
	 * <p>Finally, the uri is encoded by HttpServletResponse.encodeURL.
	 */
	public String encodeURL(String uri) throws DspException;

	/** Returns the line number of this action.
	 * It is used for throwing exception and debug purpose.
	 */
	public int getLineNumber();
}
