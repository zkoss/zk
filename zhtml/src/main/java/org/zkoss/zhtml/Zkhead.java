/* Zkhead.java

	Purpose:

	Description:

	History:
		Thu Aug 21 16:16:34     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.HtmlPageRenders;

/**
 * The component used to generate CSS and JavaScrpt declarations. By default, CSS and JavaScript
 * declarations are generated automatically (right before &lt;/head&gt;). However, if you prefer to
 * generate them in the particular location, you can use this tag.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public class Zkhead extends AbstractComponent {
	public Zkhead() {
	}

	// Component//
	public void redraw(Writer out) throws IOException {
		final Execution exec = Executions.getCurrent();
		if (exec != null) {
			if (!HtmlPageRenders.isDirectContent(exec))
				throw new UnsupportedOperationException("The parent of zkhead must be head");

			final String zktags = HtmlPageRenders.outHeaderZkTags(exec, getPage());
			if (zktags != null)
				out.write(zktags);
		}
	}

	public java.lang.Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	protected class ExtraCtrl implements org.zkoss.zk.ui.ext.render.DirectContent {
	}
}
