/* Head.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:49:25     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.io.StringWriter;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.fn.ZkFns;
import org.zkoss.zhtml.impl.AbstractTag;

/**
 * The HEAD tag.
 *
 * @author tomyeh
 */
public class Head extends AbstractTag {
	public Head() {
		super("head");
	}

	//-- super --//
	/** Don't generate the id attribute.
	 */
	protected boolean shallHideId() {
		return true;
	}

	//--Component-//
	public void redraw(java.io.Writer out) throws java.io.IOException {
		final StringWriter bufout = new StringWriter();
		super.redraw(bufout);
		final StringBuffer buf = bufout.getBuffer();

		final Execution exec = Executions.getCurrent();
		final String ATTR_ACTION = "zk_argAction";
		final String action = (String)exec.getAttribute(ATTR_ACTION);
		if (action != null) {
			int j = buf.indexOf("</head>");
			if (j < 0) j = buf.length();
			buf.insert(j, '\n')
				.insert(j, ZkFns.outLangJavaScripts(action))
				.insert(j, '\n')
				.insert(j, ZkFns.outLangStyleSheets())
				.insert(j, '\n');

			exec.removeAttribute(ATTR_ACTION); //turn off page.dsp's generation
		}

		out.write(buf.toString());
	}
}
