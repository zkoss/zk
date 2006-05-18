/* LabelLocator.java

{{IS_NOTE
	$Id: LabelLocator.java,v 1.2 2006/02/27 03:42:06 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Apr  7 14:16:39     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.resource;

import java.util.Locale;
import java.net.URL;

/**
 * A locater used to locate extra resource for {@link LabelLoader}.
 * Once registered (by {@link LabelLoader#register}), the label loader
 * will invoke {@link #locate} to locate any extra resource.
 * If so, it will load labels from it.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:42:06 $
 */
public interface LabelLocator {
	/** Returns URL for the specified locale, or null if not available.
	 * <p>It must be thread-safe.
	 */
	public URL locate(Locale locale) throws Exception;
}
