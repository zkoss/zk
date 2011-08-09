/* DirectOutputTag.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug  9 19:28:39 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zhtml.impl;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.ext.AfterCompose;

/**
 * Represents a tag that shall generate the child elements directly.
 * A typical example is {@link org.zkoss.zhtml.Script}.
 *
 * @author tomyeh
 * @since 5.0.8
 */
public class DirectOutputTag extends AbstractTag implements AfterCompose {
	private String _content;

	public DirectOutputTag(String tagnm) {
		super(tagnm);
	}

	/** Returns the widget class, "zhtml.Direct".
	 */
	public String getWidgetClass() {
		return "zhtml.Direct";
	}

	//@Override
	public void afterCompose() {
		String content = PageRenderer.childrenToContent(this);
		if (content != null)
			_content = content;
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
