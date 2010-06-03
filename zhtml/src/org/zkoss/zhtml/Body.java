/* Body.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:50:07     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.util.Collection;
import java.io.StringWriter;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zhtml.impl.PageRenderer;

/**
 * The BODY tag.
 *
 * @author tomyeh
 */
public class Body extends AbstractTag {
	public Body() {
		super("body");
	}

	//--Component-//
	public void redraw(java.io.Writer out) throws java.io.IOException {
		final StringWriter bufout = new StringWriter();
		super.redraw(bufout);

		final StringBuffer buf = bufout.getBuffer();
		final Execution exec = Executions.getCurrent();
		if (exec != null)
			Utils.addAllZkTags(exec, getPage(), buf, "body");

		out.write(buf.toString());
		out.write('\n');
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Html))
			throw new UiException("Body's parent must be Html, not "+parent);
		super.beforeParentChanged(parent);
	}
}
