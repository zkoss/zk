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

import java.io.StringWriter;
import java.lang.Object;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zhtml.impl.PageRenderer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

/**
 * The HTML tag.
 *
 * @author tomyeh
 */
public class Html extends AbstractTag {
	public Html() {
		super("html");
	}
	/**
	 * Returns the manifest of this html tag.
	 * @since 8.0.3
	 */
	public String getManifest() {
		return (String) getDynamicProperty("manifest");
	}

	/**
	 * Sets the manifest of this html tag.
	 * @since 8.0.3
	 */
	public void setManifest(String manifest) throws WrongValueException {
		setDynamicProperty("manifest", manifest);
	}
	/**
	 * Returns the xmlns of this html tag.
	 * @since 8.0.3
	 */
	public String getXmlns() {
		return (String) getDynamicProperty("xmlns");
	}

	/**
	 * Sets the xmlns of this html tag.
	 * @since 8.0.3
	 */
	public void setXmlns(String xmlns) throws WrongValueException {
		setDynamicProperty("xmlns", xmlns);
	}
	// -- super --//
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
		super.redraw(bufout);
		PageRenderer.afterRenderHtml(exec, page, bufout, ret);

		final StringBuffer buf = bufout.getBuffer();
		if (exec != null)
			Utils.addAllZkTags(exec, getPage(), buf, "html");

		out.write(buf.toString());
	}

	/**
	 * Don't generate the id attribute.
	 */
	protected boolean shallHideId() {
		return true;
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null)
			throw new UiException("Html must be the root component, not " + parent);
		super.beforeParentChanged(parent);
	}
}
