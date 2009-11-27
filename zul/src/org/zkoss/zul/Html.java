/* Html.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 25 11:39:49     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.Writer;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zul.impl.XulElement;

/**
 * A comonent used to embed the browser native content (i.e., HTML tags)
 * into the output sent to the browser.
 * The browser native content is specified by {@link #setContent}.
 *
 * <p>Notice that {@link Html} generates HTML SPAN to enclose
 * the embedded HTML tags. Thus, you can specify the style
 * ({@link #getStyle}), tooltip {@link #getTooltip} and so on.
 *
 * <pre><code>&lt;html style="border: 1px solid blue"&gt;&lt;![CDATA[
 * &lt;ul&gt;
 *  &lt;li&gt;It is in a SPAN tag.&lt;/li&gt;
 * &lt;/ul&gt;
 *]]&gt;&lt;/html&gt;</code></pre>
 *
 * <p>The generated HTML tags will look like:
 * <pre><code>&lt;SPAN id="xxx" style="border: 1px solid blue"&gt;
 * &lt;ul&gt;
 *  &lt;li&gt;It is in a SPAN tag.&lt;/li&gt;
 * &lt;/ul&gt;
 *&lt;/SPAN&gt;</code></pre>
 *
 * <p>Since SPAN is used to enclosed the embedded HTML tags, so
 * the following is incorrect.
 *
 * <pre><code>&lt;html&gt;&lt;![CDATA[
 * &lt;table&gt;
 *  &lt;tr&gt;
 *   &lt;td&gt; &lt;-- Incomplete since it is inside SPAN --&gt;
 *]]&gt;&lt;/html&gt;
 *
 *&lt;textbox/&gt;
 *
 *&lt;html&gt;&lt;![CDATA[
 *   &lt;/td&gt;
 *  &lt;/tr&gt;
 * &lt;/table&gt;
 *]]&gt;&lt;/html&gt;</code></pre>
 *
 * <p>If you need to generate the HTML tags directly
 * without enclosing with SPAN, you can use the Native namespace,
 * http://www.zkoss.org/2005/zk/native.
 * Refer to the Developer's Guide for more information.
 *
 * <p>A non-XUL extension.
 *
 * @author tomyeh
 */
public class Html extends XulElement implements org.zkoss.zul.api.Html {
	private String _content = "";

	/** Contructs a {@link Html} component to embed HTML tags.
	 */
	public Html() {
	}
	/** Contructs a {@link Html} component to embed HTML tags
	 * with the specified content.
	 */
	public Html(String content) {
		_content = content != null ? content: "";
	}

	/** Returns the embedded content (i.e., HTML tags).
	 * <p>Default: empty ("").
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the embedded content (i.e., HTML tags).
	 */
	public void setContent(String content) {
		if (content == null) content = "";
		if (!Objects.equals(_content, content)) {
			_content = content;
			smartUpdate("content", getContent());
			//allow deriving to override getContent()
		}
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		String cnt = getContent();
			//allow deriving to override getContent()
		if (cnt.length() > 0) {
			final HtmlPageRenders.RenderContext rc =
				HtmlPageRenders.getRenderContext(null);
			if (rc != null && rc.crawlable) {
				final Writer cwout = rc.temp;
				cwout.write("<div id=\"");
				cwout.write(getUuid());
				cwout.write("\">");
				cwout.write(cnt);
				cwout.write("</div>\n");
				cnt = null; //means already generated
			}
			if (cnt == null) renderer.render("z$ea", "content");
			else render(renderer, "content", cnt);
		}
	}

	/** Default: not childable.
	 */
	protected boolean isChildable() {
		return false;
	}
}
