/* Style.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 20 15:17:40     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Iterator;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;

/**
 * The style component used to specify CSS styles for the owner desktop.
 *
 * <p>Note: a style component can appear anywhere in a ZUML page, but it
 * affects all components in the same desktop.
 *
 * <p>There are three formats when used in a ZUML page:
 *
 * <p>Method 1: Specify the URL of the CSS file
 * <pre><code>&lt;style src="my.css"/&gt;
 * </code></pre>
 *
 * <p>Method 2: Specify the CSS directly
 * <pre><code>&lt;style&gt;
 * .mycls {
 *  border: 1px outset #777;
 * }
 *&lt;/style&gt;
 * </code></pre>
 *
 * <p>Method 3: Specify the CSS by use of the content
 * property ({@link #setContent}).
 * <pre><code>&lt;style&gt;
 * &lt;attribute name="content"&gt;
 * .mycls {
 *  border: 1px outset #777;
 * }
 * &lt;/attribute&gt;
 *&lt;/style&gt;
 * </code></pre>
 *
 * @author tomyeh
 */
public class Style extends AbstractComponent {
	private String _src;
	private String _content;

	public Style() {
	}
	/**
	 * @param src the URI of an external style sheet.
	 */
	public Style(String src) {
		setSrc(src);
	}

	/** Returns the URI of an external style sheet.
	 *
	 * <p>Default: null. If null, the children's
	 * (must be instances of {@link Label}) value ({@link Label#getValue})
	 * is used as the content of styles.
	 * If not null, the HTML LINK tag is generated to ask the browser
	 * to load the specified style sheet.
	 *
	 * <p>Note: If not null, the content of children are ignored.
	 */
	public String getSrc() {
		return _src;
	}
	/** Sets the URI of an external style sheet.
	 *
	 * @param src the URI of an external style sheet, or null to use
	 * the content of children ({@link Label#getValue}) instead.
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;
		if (!Objects.equals(_src, src)) {
			_src = src;
			invalidate();
		}
	}

	/** Returns the content of the style element.
	 * By content we mean the CSS that will be enclosed
	 * by the HTML STYLE element.
	 *
	 * <p>Default: null.
	 *
	 * @since 2.5.0
	 */
	public String getContent() {
		childToContent();
		return _content;
	}
	/** Sets the content of the style element.
	 * By content we mean the CSS that will be enclosed
	 * by the HTML STYLE element.
	 *
	 * @since 2.5.0
	 */
	public void setContent(String content) {
		childToContent();

		if (content != null && content.length() == 0)
			content = null;

		if (!Objects.equals(_content, content)) {
			_content = content;
			invalidate();
		}
	}
	/** Converts children to the content.
	 */
	private void childToContent() {
		if (_content == null) {
			StringBuffer sb = null;
			final List children = getChildren();
			while (!children.isEmpty()) {
				final String val = ((Label)children.remove(0)).getValue();
				if (val.length() > 0)
					if (sb == null) sb = new StringBuffer(val);
					else sb.append(val);
			}
			if (sb != null) {
				_content = sb.toString();
				invalidate();
			}
		}
	}

	//Component//
	/** Used only to enable UI engine to set the content with a simple
	 * and ituitive way:
	 *
	 * <pre><code>&lt;style&gt;
	 * .mycls {
	 *  border: 1px outset #777;
	 * }
	 *&lt;/style&gt;
	 * </code></pre>
	 *
	 * <p>Application developer shall use {@link #setContent} instead.
	 *
	 * <p>The child will removed later, so application shall not depend
	 * on this method.
	 */
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Label))
			throw new UiException("Unsupported child for style: "+child);
		if (_content != null)
			throw new UiException("insertBefore used by UI engine only");

		//Note: we cannot copy child's value to _content here, since
		//UI engine calls setParent first and then setValue.
		if (super.insertBefore(child, insertBefore)) {
			invalidate();
			return true;
		}
		return false;
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		invalidate();
	}

	public void redraw(java.io.Writer out) throws java.io.IOException {
		final boolean ie = Executions.getCurrent().isExplorer();
		if (ie) {
			//IE: unable to look back LINK or STYLE with ID
			out.write("<div id=\"");
			out.write(getUuid());
			out.write("\">");
		}

		if (_src != null) {
			out.write("\n<link rel=\"stylesheet\" type=\"text/css\" href=\"");
			out.write(getDesktop().getExecution().encodeURL(_src));
			if (!ie) {
				out.write("\" id=\"");
				out.write(getUuid());
			}
			out.write("\"/>");
		} else {
			out.write("\n<style type=\"text/css\"");
			if (!ie) {
				out.write(" id=\"");
				out.write(getUuid());
				out.write('"');
			}
			out.write(">\n");

			final String content = getContent();
			if (content != null) {
				out.write(content);
				out.write('\n');
			}

			out.write("</style>");
		}

		if (ie)
			out.write("</div>\n");
	}
}
