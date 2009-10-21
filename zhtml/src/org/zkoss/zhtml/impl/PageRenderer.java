/* PageRenderer.java

	Purpose:
		
	Description:
		
	History:
		Sat Jan  3 17:58:50     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zhtml.impl;

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
import org.zkoss.zk.ui.impl.Attributes;

/**
 * The page render for ZHTML pages.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class PageRenderer implements org.zkoss.zk.ui.sys.PageRenderer {
	/** An execution attribute to retrieve the render context.
	 */
	private static final String ATTR_RENDER_CONTEXT
		= "org.zkoss.zhtml.renderContext";

	/** Tests if it is rendering the complete page and the content shall
	 * be generated directly.
	 * @param exec the execution. If null, Executions.getCurrent() is assumed.
	 * If no execution, false is returned.
	 */
	public static boolean isDirectContent(Execution exec) {
		if (exec == null) {
			exec = Executions.getCurrent();
			if (exec == null) return false;
		}
		final RenderContext rc = getRenderContext(exec);
		return rc != null && rc.directContent;
	}
	/** Returns the render context, or null if not available.
	 * The render context is available only if the ZHTML page is rendered
	 * directly (rather than via inclusion).
	 * @param exec the execution. If null, Executions.getCurrent() is assumed.
	 */
	public static RenderContext getRenderContext(Execution exec) {
		if (exec == null) exec = Executions.getCurrent();
		return exec != null ?
			(RenderContext)exec.getAttribute(ATTR_RENDER_CONTEXT): null;
	}

	//PageRenderer//
	public void render(Page page, Writer out) throws IOException {
		final Execution exec = Executions.getCurrent();
		final String ctl =
			(String)exec.getAttribute(Attributes.ATTR_PAGE_REDRAW_CONTROL);
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
		else {
			exec.setAttribute(Attributes.ATTR_PAGE_REDRAW_CONTROL, "complete");
			renderComplete(exec, page, out);
		}
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
	 * @param au whether it is caued by an asynchronous update
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
		out.write(HtmlPageRenders.outResponseJavaScripts(exec));
	}
	/** Renders the page if {@link Page#isComplete} is true.
	 * In other words, the page content contains HTML/BODY tags.
	 */
	protected void renderComplete(Execution exec, Page page, Writer out)
	throws IOException {
		final Object ret = beforeRenderHtml(exec, page, out);

		for (Iterator it = page.getRoots().iterator(); it.hasNext();)
			((ComponentCtrl)it.next()).redraw(out);

		afterRenderHtml(exec, page, out, ret);
	}

	/** Prepares for rendering a complete ZHTML page.
	 * After rendering, the caller shall also invoke {@link #afterRenderHtml}.
	 * Furthermore, the return value of this method shall be passed as
	 * the <code>param</code> argument of {@link #afterRenderHtml}.
	 *
	 * @see #afterRenderHtml
	 */
	public static
	Object beforeRenderHtml(Execution exec, Page page, Writer out)
	throws IOException {
		RenderContext rc = getRenderContext(exec);
		if (rc != null)
			return null; //already been called

		rc = new RenderContext();
		exec.setAttribute(ATTR_RENDER_CONTEXT, rc);
		HtmlPageRenders.setContentType(exec, page);

		write(out, HtmlPageRenders.outFirstLine(page)); //might null
		write(out, HtmlPageRenders.outDocType(page)); //might null
		return rc;
	}
	/** Ends and cleans up the rendering of a complete ZHTML page.
	 *
	 * @param param the value returned by {@link #beforeRenderHtml}.
	 */
	public static
	void afterRenderHtml(Execution exec, Page page, Writer out, Object param)
	throws IOException {
		if (param == null)
			return; //nothing to do

		final RenderContext rc = (RenderContext)param;
		final String rcs = rc.complete();
		if (rcs.length() > 0) {
			if (out instanceof StringWriter) {
				final StringBuffer buf = ((StringWriter)out).getBuffer();
				final int j = buf.lastIndexOf("</body>");
				if (j >= 0) buf.insert(j, rcs);
				else buf.append(rcs);
			} else {
				out.write(rcs);
			}
		}

		write(out, HtmlPageRenders.outHeaderZkTags(exec, page));
		writeln(out, HtmlPageRenders.outUnavailable(exec));

		exec.setAttribute(ATTR_RENDER_CONTEXT, null);
	}
}
