/* Script.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct 15 12:15:47     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;
import java.io.Writer;

import org.zkoss.lang.Objects;
import org.zkoss.html.HTMLs;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.sys.ComponentRedraws;

/**
 * A component to generate script codes that will be evaluated at the client.
 * It is similar to HTML SCRIPT tag, except the defer option ({@link #setDefer})
 * will cause the evaluation of JavaScript until the widget has been
 * instantiated and mounted to the DOM tree.
 *
 * <p>Note: it is the script codes running at the client, not at the
 * server. Don't confuse it with the <code>zscript</code> element.
 *
 * <p>There are several way to embed script codes in a ZUML page:
 *
 * <p>Approach 1: Specify the URL of the JS file without defer.
 * The JavaScript codes are evaluated as soon as the file is loaded.
 * <pre><code>&lt;script src="my.js"/&gt;
 * </code></pre>
 *
 * <p>Approach 2: Specify the JavaScript codes directly without defer.
 * The JavaScript codes are evaluated immediately before the widget is
 * instantiated, so you cannot access any widget. Rather, it is used to
 * do desktop-level initialization, such as defining a widget class, and 
 * a global function.
 * <pre><code>&lt;script defer="true"&gt;
 * zk.$package('foo');
 * zk.load('zul.wgt', function () {
 * foo.Foo = zk.$extends(zul.Widget, {
 * //...
 *&lt;/script&gt;
 * </code></pre>
 *
 * <p>Approach 3: Specify the JavaScript codes directly with defer.
 * The JavaScipt codes are evaluated after the widget is instantiated and
 * mounted. Moreover, <code>this</code> references to the script widget,
 * so you can access the widgets as follows.
 * <pre><code>&lt;script defer="true"&gt;
 * this.getFellow('l').setValue('new value');
 * //...
 *&lt;/script&gt;
 * </code></pre>
 *
 * <p>Alternative to {@link Script}, you can use the script directive
 * as shown below..
 *
 * <pre><code>&lt:?script src="/js/mine.js"?/&gt;
 *&lt:?script content="jq.IE6_ALPHAFIX=/.png/"?/&gt;
 </code></pre>
 *
 * @author tomyeh
 */
public class Script extends AbstractComponent implements org.zkoss.zul.api.Script {
	private String _src, _charset;
	private String _content;
	private String _packages;
	private boolean _defer;

	public Script() {
	}
	/** Constructs a script component with the specified content.
	 * @param content the content (the code snippet).
	 * @since 5.1.0
	 */
	public Script(String content) {
		setContent(content);
	}

	/** @deprecated As of release 5.0.0, it is meaningless since
	 * text/javascript is always assumed.
	 */
	public String getType() {
		return "text/javascript";
	}
	/** @deprecated As of release 5.0.0, it is meaningless since
	 * text/javascript is always assumed.
	 */
	public void setType(String type) {
	}
	/** Returns the character enconding of the source.
	 * It is used with {@link #getSrc}.
	 *
	 * <p>Default: null.
	 */
	public String getCharset() {
		return _charset;
	}
	/** Sets the character encoding of the source.
	 * It is used with {@link #setSrc}.
	 */
	public void setCharset(String charset) {
		if (charset != null && charset.length() == 0)
			charset = null;

		if (!Objects.equals(_charset, charset)) {
			_charset = charset;
			smartUpdate("charset", _charset);
		}
	}

	/** Returns the URI of the source that contains the script codes.
	 * <p>Default: null.
	 */
	public String getSrc() {
		return _src;
	}
	/** Sets the URI of the source that contains the script codes.
	 *
	 * <p>You either add the script codes directly with the {@link Label}
	 * children, or
	 * set the URI to load the script codes with {@link #setSrc}.
	 * But, not both.
	 *
	 * @param src the URI of the source that contains the script codes
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;

		if (!Objects.equals(_src, src)) {
			_src = src;
			smartUpdate("src", new EncodedSrcURL());
		}
	}

	/** Returns whether to defer the execution of the script codes
	 * until the widget is instantiated and mounted.
	 *
	 * <p>Default: false.
	 *
	 * <p>Specifying false (default), if you want to do the desktop-level
	 * (or class-level) initialization, such as defining a widget class
	 * or a global function.
	 * <p>Specifying true, if you want to access widgets. Notice that
	 * <code>this</code> references to this script widget.
	 */
	public boolean isDefer() {
		return _defer;
	}
	/** Sets whether to defer the execution of the script codes.
	 * @see #isDefer
	 */
	public void setDefer(boolean defer) {
		_defer = defer;
		//no smart update nor invalidate, since it is meaningless if
		//the peer widget has been created
	}

	/** Returns the content of the script element.
	 * By content we mean the JavaScript codes that will be enclosed
	 * by the HTML SCRIPT element.
	 *
	 * <p>Default: null.
	 *
	 * <p>Deriving class can override this method to return whatever
	 * it prefers (ingored if null).
	 * @since 3.0.0
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the content of the script element.
	 * By content we mean the JavaScript codes that will be enclosed
	 * by the HTML SCRIPT element.
	 *
	 * @since 3.0.0
	 */
	public void setContent(String content) {
		if (content != null && content.length() == 0)
			content = null;

		if (!Objects.equals(_content, content)) {
			_content = content;
			smartUpdate("content", getContent());
				//allow deriving to override getContent()
		}
	}

	/** Returns the list of packages to load before evaluating the script
	 * defined in {@link #getContent}.
	 * It is meaning only if {@link #getContent} not null.
	 * <p>Default: null.
	 * @since 5.0.0
	 */
	public String getPackages() {
		return _packages;
	}
	/** Sets the list of packages to load before evaluating the script
	 * defined in {@link #getContent}.
	 * If more than a package to load, separate them with comma.
	 * @since 5.0.0
	 */
	public void setPackages(String packages) {
		if (packages != null && packages.length() == 0)
			packages = null;

		if (!Objects.equals(_packages, packages)) {
			_packages = packages;
			smartUpdate("packages", _packages);
		}
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		final String cnt = getContent();
			//allow deriving to override getContent()
		if (cnt != null)
			if (_defer)
				renderer.renderDirectly("content", "function(){\n" + cnt + "\n}");
			else {
				Writer out = ComponentRedraws.getScriptBuffer();
				out.write(cnt);
				out.write('\n');
			}

		if (_src != null) {
			final HtmlPageRenders.RenderContext rc =
				_defer ? null: HtmlPageRenders.getRenderContext(null);
			if (rc != null) {
				final Writer cwout = rc.perm;
				cwout.write("\n<script type=\"text/javascript\" src=\"");
				cwout.write(getEncodedSrcURL());
				cwout.write('"');
				if (_charset != null) {
					cwout.write(" charset=\"");
					cwout.write(_charset);
					cwout.write('"');
				}
				cwout.write(">\n</script>\n");
			} else
				render(renderer, "src", getEncodedSrcURL());
		}

		render(renderer, "charset", _charset);
		render(renderer, "packages", _packages);
	}
	/** Not childable. */
	protected boolean isChildable() {
		return false;
	}

	private String getEncodedSrcURL() {
		if (_src == null)
			return null;

		final Desktop dt = getDesktop(); //it might not belong to any desktop
		return dt != null ? dt.getExecution().encodeURL(_src): null;
	}
	private class EncodedSrcURL implements org.zkoss.zk.ui.util.DeferredValue {
		public Object getValue() {
			return getEncodedSrcURL();
		}
	}
}
