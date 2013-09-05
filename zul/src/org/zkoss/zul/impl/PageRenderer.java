/* PageRenderer.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 14 17:31:02     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;

/**
 * The page render for ZUL pages.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class PageRenderer implements org.zkoss.zk.ui.sys.PageRenderer {
	public void render(Page page, Writer out) throws IOException {
		final Execution exec = Executions.getCurrent();
		final String ctl = ExecutionsCtrl.getPageRedrawControl(exec);
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
			renderPage(exec, page, out, au);
		else
			renderDesktop(exec, page, out);
	}
	/** Renders the desktop and the page.
	 */
	protected
	void renderDesktop(Execution exec, Page page, Writer out)
	throws IOException {
		HtmlPageRenders.setContentType(exec, page);

		final PageCtrl pageCtrl = (PageCtrl)page;
		write(out, HtmlPageRenders.outFirstLine(exec, page)); //might null
		write(out, HtmlPageRenders.outDocType(exec, page)); //might null
		Double number = exec.getBrowser("mobile");

		out.write("<html");
		if (number == null || number.intValue() == 0) {
			Double ie = exec.getBrowser("ie");
			// let ie <= 8 support VML on javascript
			if (ie != null && ie < 9)
				out.write(" xmlns:v=\"urn:schemas-microsft.com:vml\"");
			
			write(out, pageCtrl.getRootAttributes());
			out.write(">\n<head>\n"
					+ "<meta http-equiv=\"Pragma\" content=\"no-cache\" />\n"
					+ "<meta http-equiv=\"Expires\" content=\"-1\" />\n"
					+ "<title>");
		} else {
			write(out, pageCtrl.getRootAttributes());
			out.write(">\n<head>\n"
					+ "<meta http-equiv=\"Pragma\" content=\"no-cache\" />\n"
					+ "<meta http-equiv=\"Expires\" content=\"-1\" />\n");
			
			String viewport = page.getViewport();
			if (!"auto".equals(viewport))
				out.write("<meta name=\"viewport\" content=\"" + viewport + "\" > \n");
			else if (!"true".equals(Library.getProperty("org.zkoss.zul.tablet.meta.viewport.disabled", "false")))
				out.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\" > \n");
			
			out.write("<title>");
		}
		write(out, page.getTitle());
		out.write("</title>\n");
		outHeaders(exec, page, out);
		out.write("</head>\n");

		out.write("<body>\n");
		HtmlPageRenders.outPageContent(exec, page, out, false);
		writeln(out, HtmlPageRenders.outUnavailable(exec));
		out.write("\n</body>\n</html>\n");
	}
	private static void outHeaders(Execution exec, Page page, Writer out)
	throws IOException {
		out.write(HtmlPageRenders.outHeaders(exec, page, true));
		out.write(HtmlPageRenders.outLangStyleSheets(exec, null, null));
		out.write(HtmlPageRenders.outLangJavaScripts(exec, null, null));
		out.write(HtmlPageRenders.outHeaders(exec, page, false));
	}
	private static void write(Writer out, String s) throws IOException {
		if (s != null) out.write(s);
	}
	private static void writeln(Writer out, String s) throws IOException {
		if (s != null) {
			out.write(s);
			out.write('\n');
		}
	}
	/** Renders the page if {@link Page#isComplete} is false.
	 *
	 * @param au whether it is caused by an asynchronous update
	 */
	protected void renderPage(Execution exec, Page page, Writer out, boolean au)
	throws IOException {
		if (!au) {
			out.write(HtmlPageRenders.outLangStyleSheets(exec, null, null));
			out.write(HtmlPageRenders.outLangJavaScripts(exec, null, null));
		}

		HtmlPageRenders.outPageContent(exec, page, out, au);
		if (!au && ((PageCtrl)page).getOwner() == null)
			writeln(out, HtmlPageRenders.outUnavailable(exec));
	}
	/** Renders the page if {@link Page#isComplete} is true.
	 * In other words, the page content contains HTML/BODY tags.
	 */
	protected void renderComplete(Execution exec, Page page, Writer out)
	throws IOException {
		write(out, HtmlPageRenders.outFirstLine(exec, page)); //might null
		write(out, HtmlPageRenders.outDocType(exec, page)); //might null
		HtmlPageRenders.setContentType(exec, page);

		for (Component root = page.getFirstRoot(); root != null; root = root.getNextSibling())
			((ComponentCtrl)root).redraw(out);

		write(out, HtmlPageRenders.outHeaderZkTags(exec, page));
		writeln(out, HtmlPageRenders.outUnavailable(exec));
	}
}
