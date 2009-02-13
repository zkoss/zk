/* DspContext.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:42:05     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp;

import java.io.Writer;

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

	/** Changes the writer of this context to the specified one.
	 *
	 * @param out the new writer. If null, it is restored to
	 * the default one.
	 */
	public void setOut(Writer out);
}
