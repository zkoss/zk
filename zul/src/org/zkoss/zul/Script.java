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

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.AbstractComponent;

/**
 * A component to generate script codes that will be evaluated at the client.
 * It is similar to HTML SCRIPT tag, except it is not evaluated immediately.
 * Rather, it is evaluated after all widgets are created and mounted
 * to the DOM tree.
 *
 * <p>Note: it is the script codes running at the client, not at the
 * server. Don't confuse it with the <code>zscript</code> element.
 *
 * <p>There are several way to embed script codes in a ZUML page:
 *
 * <p>Approach 1: Specify the URL of the JS file
 * <pre><code>&lt;script src="my.js"/&gt;
 * </code></pre>
 *
 * <p>Approach 2: Specify the JavaScript codes directly
 * <pre><code>&lt;script&gt;
 * this.getFellow('l').setValue('new value');
 * //...
 *&lt;/script&gt;
 * </code></pre>
 *
 * <p>Notice that, since 5.0, the JavaScript codes are evaluated after
 * the widgets are created and mounted. In other words, it is always deferred.
 * Moreover, codes specified in Approach 2 are evaluated as a method of
 * the peer widget. Thus, you can access this widget with <code>this</code>
 *
 * <p>Notice that JavaScript codes specified in Approach 1 might be evaluated
 * later than codes in other formats since it takes another HTTP request to
 * load the JavaScript file.
 *
 * <p>If you want to run the codes immediately without waiting the widgets
 * to be created, you can use the script directive, or
 * specify it in language addon.
 *
 * <pre><code>&lt:?script src="/js/mine.js"?/&gt;</code></pre>
 *
 * @author tomyeh
 */
public class Script extends AbstractComponent implements org.zkoss.zul.api.Script {
	private String _src, _charset;
	private String _content;
	private String _packages;

	public Script() {
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

	/** @deprecated As of release 5.0.0, it is meaningless since it is always
	 * deferred
	 */
	public boolean isDefer() {
		return true;
	}
	/** @deprecated As of release 5.0.0, it is meaningless since it is always
	 * deferred
	 */
	public void setDefer(boolean defer) {
	}

	/** Returns the content of the script element.
	 * By content we mean the JavaScript codes that will be enclosed
	 * by the HTML SCRIPT element.
	 *
	 * <p>Default: null.
	 *
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
			smartUpdate("content", _content);
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
	//-- Component --//
	/** Not childable. */
	protected boolean isChildable() {
		return false;
	}
	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		if (_content != null)
			renderer.renderDirectly("content", "function(){\n" + _content + '}');
		render(renderer, "src", getEncodedSrcURL());
		render(renderer, "charset", _charset);
		render(renderer, "packages", _packages);
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
