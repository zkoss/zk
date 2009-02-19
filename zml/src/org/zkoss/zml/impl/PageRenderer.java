/* PageRenderer.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan  9 19:39:28     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zml.impl;

import java.util.Collection;
import java.util.Iterator;
import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;

/**
 * The page render for XML pages.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class PageRenderer implements org.zkoss.zk.ui.sys.PageRenderer {
	//PageRenderer//
	public void render(Page page, Writer out) throws IOException {
		final Execution exec = Executions.getCurrent();
		final String ctl =
			(String)exec.getAttribute(PageCtrl.ATTR_REDRAW_CONTROL);
		boolean au = exec.isAsyncUpdate(null);
		if (!au && (page.isComplete() || "complete".equals(ctl))) {
			renderComplete(exec, page, out);
			return;
		}

		boolean pageOnly = au;
		if (!pageOnly)
			pageOnly = (exec.isIncluded() || "page".equals(ctl))
				&& !"desktop".equals(ctl);

		if (pageOnly)
			renderPage(exec, page, out);
		else
			renderComplete(exec, page, out);
	}
	private static void write(Writer out, String s) throws IOException {
		if (s != null) out.write(s);
	}
	/** Renders the page if {@link Page#isComplete} is false.
	 */
	protected void renderPage(Execution exec, Page page, Writer out)
	throws IOException {
		for (Iterator it = page.getRoots().iterator(); it.hasNext();)
			((ComponentCtrl)it.next()).redraw(out);

	}
	/** Renders the page if {@link Page#isComplete} is true.
	 * In other words, the page content contains HTML/BODY tags.
	 */
	protected void renderComplete(Execution exec, Page page, Writer out)
	throws IOException {
		HtmlPageRenders.setContentType(exec, page);
		write(out, HtmlPageRenders.outFirstLine(page));
		write(out, HtmlPageRenders.outDocType(page));
		renderPage(exec, page, out);
	}
}
