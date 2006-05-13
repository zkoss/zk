/* DSPContext.java

{{IS_NOTE
	$Id: DSPContext.java,v 1.9 2006/03/09 08:51:07 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:42:05     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp;

import java.io.Writer;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.FunctionMapper;

import com.potix.util.resource.Locator;
import com.potix.web.el.ELContext;

/**
 * The context used with {@link Interpreter#interpret}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.9 $ $Date: 2006/03/09 08:51:07 $
 * @see Interpreter
 */
public interface DSPContext extends ELContext {
	/** Returns the locator for loading resources, such as taglib.
	 * You might return null if the page not referencing external resources.
	 *
	 * <p>To load the resource from a web application, use
	 * {@link com.potix.web.util.resource.ServletContextLocator}
	 * To load the resource from class path, use
	 * com.potix.util.resource.Resources.getDefault().
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
