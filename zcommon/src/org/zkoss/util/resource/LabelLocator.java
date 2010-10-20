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
 * Once registered (by {@link Labels#register(LabelLocator)}), the label loader
 * will invoke {@link #locate} to locate any extra resource.
 *
 * <p>If the resource is not easy to be represented in URL, you could implement
 * {@link LabelLocator2} instead.
 *
 * @author tomyeh
 * @see LabelLocator2
 */
public interface LabelLocator {
	/** Returns URL containing the labels for the specified locale,
	 * or null if not available.
	 * <p>It must be thread-safe.
	 */
	public URL locate(Locale locale) throws Exception;
}
