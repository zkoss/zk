/* LabelLocator2.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 12 10:33:32 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.util.resource;

import java.util.Locale;
import java.io.InputStream;

/**
 * A locater used to locate extra resource for {@link Labels} into
 * an input stream.
 * Once registered (by {@link Labels#register(LabelLocator2)}), the label loader
 * will invoke {@link #locate} to locate any extra resource.
 *
 * @author tomyeh
 * @since 5.0.5
 */
public interface LabelLocator2 {
	/** Returns the input stream containing the label for the specified locale,
	 * or null if not available.
	 * <p>It must be thread-safe.
	 */
	public InputStream locate(Locale locale);
	/** Returns the encoding charset, or null if it is the same as the system default.
	 * <p>The system default is decided by the library property called
	 * org.zkoss.util.label.web.charset. If not specified, UTF-8 is assumed.
	 */
	public String getCharset();
}
