/* ServletLabelLocator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Apr  8 19:51:08     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.util.Locale;
import java.io.IOException;
import java.net.URL;
import javax.servlet.ServletContext;

import org.zkoss.util.resource.LabelLocator;

/**
 * Used by Lables to load labels from a servlet context.
 *
 * @author tomyeh
 */
public class ServletLabelLocator implements LabelLocator {
	private final ServletContext _ctx;
	public ServletLabelLocator(ServletContext ctx) {
		if (ctx == null)
			throw new IllegalArgumentException("null");
		_ctx = ctx;
	}

	//-- LabelLocator --//
	public URL locate(Locale locale) throws IOException {
		return _ctx.getResource(
			locale == null ? "/WEB-INF/i3-label.properties":
				"/WEB-INF/i3-label_" + locale + ".properties");
	}

	//-- Object --//
	public int hashCode() {
		return _ctx.hashCode();
	}
	public boolean equals(Object o) {
		return o instanceof ServletLabelLocator
			&& ((ServletLabelLocator)o)._ctx.equals(_ctx);
	}
}
