/* Html.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:44:36     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.lang.Object;
import java.io.StringWriter;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zhtml.impl.PageRenderer;

/**
 * The HTML tag.
 *
 * @author tomyeh
 */
public class Html extends AbstractTag {
	public Html() {
		super("html");
	}

	//-- super --//
	public void redraw(java.io.Writer out) throws java.io.IOException {
		final Execution exec = Executions.getCurrent();
		final StringWriter bufout = new StringWriter();
		final Page page = getPage();
		final Object ret = PageRenderer.beforeRenderHtml(exec, page, bufout);
		super.redraw(bufout);
		PageRenderer.afterRenderHtml(exec, page, bufout, ret);

		final StringBuffer buf = bufout.getBuffer();
		if (exec != null)
			Utils.addAllZkTags(exec, getPage(), buf, "html");

		out.write(buf.toString());
	}
	/** Don't generate the id attribute.
	 */
	protected boolean shallHideId() {
		return true;
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null)
			throw new UiException("Html must be the root component, not "+parent);
		super.beforeParentChanged(parent);
	}
}
