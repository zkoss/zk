/* ContentTag.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug  9 19:28:39 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zhtml.impl;

import org.zkoss.zk.ui.Execution;

/**
 * Represents a tag that shall generate the child elements directly.
 * A typical example is {@link org.zkoss.zhtml.Script}.
 *
 * @author tomyeh
 * @since 5.0.8
 */
public class ContentTag extends AbstractTag {
	private String _content = "";

	public ContentTag(String tagnm) {
		super(tagnm);
	}
	public ContentTag(String tagnm, String content) {
		this(tagnm);
		_content = content != null ? content: "";
	}

	/** Returns the widget class, "zhtml.Content".
	 */
	public String getWidgetClass() {
		return "zhtml.Content";
	}

	/** Returns the content.
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the content.
	 */
	public void setContent(String content) {
		if (content == null) content = "";
		if (!content.equals(_content)) {
			_content = content;
			smartUpdate("content", content);
		}
	}

	//@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "content", _content);
	}
	//@Override
	protected void redrawChildrenDirectly(TagRenderContext rc, Execution exec,
	java.io.Writer out) throws java.io.IOException {
		out.write(_content);
		super.redrawChildrenDirectly(rc, exec, out);
	}
}
