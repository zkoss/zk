/* LabelLocator.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr  7 14:16:39     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import java.util.Locale;
import java.net.URL;

/**
 * A locater used to locate extra resource for {@link Labels}.
 * Once registered (by {@link Labels#register}), the label loader
 * will invoke {@link #locate} to locate any extra resource.
 * If so, it will load labels from it.
 *
 * @author tomyeh
 */
public interface LabelLocator {
	/** Returns URL for the specified locale, or null if not available.
	 * <p>It must be thread-safe.
	 */
	public URL locate(Locale locale) throws Exception;
}
