/* Html.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:44:36     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.lang.Object;
import java.io.StringWriter;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zhtml.impl.PageRenderer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zul.Include;
/**
 * The HTML tag.
 *
 * @author tomyeh
 */
public class Html extends AbstractTag {
	private boolean _isInstant = false;
	
	public Html() {
		super("html");
	}

	//-- super --//
	public void invalidate() {
		final Execution exec = Executions.getCurrent();
		if (exec != null && exec.isAsyncUpdate(getPage()))
			throw new UnsupportedOperationException("html.invalidate() not allowed");
		super.invalidate();
	}
	public void redraw(java.io.Writer out) throws java.io.IOException {
		final Execution exec = Executions.getCurrent();
		final StringWriter bufout = new StringWriter();
		final Page page = getPage();
		final Object ret = PageRenderer.beforeRenderHtml(exec, page, bufout);
		
		// ZK-2567: when included in instant mode, set direct content render to false
		if (this._isInstant) {
			HtmlPageRenders.setDirectContent(exec, false);
		}
		super.redraw(bufout);
		
		// ZK-2567: shouldn't call after render html if it's rendered in instant mode
		if (!this._isInstant) {
			PageRenderer.afterRenderHtml(exec, page, bufout, ret);
		}

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
		// ZK-2567: should also support include in instant mode
		if (parent instanceof Include) {
			this._isInstant = true;
		}
		if (parent != null && !this._isInstant)
			throw new UiException("Html must be the root component, not "+parent);
		super.beforeParentChanged(parent);
	}
}
