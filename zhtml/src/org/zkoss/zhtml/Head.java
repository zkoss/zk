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

import org.zkoss.zk.ui.Desktop;
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

		final String zktags = outZKHtmlTags(getDesktop());
		if (zktags != null) {
			final int j = buf.lastIndexOf("</head>");
			if (j >= 0) buf.insert(j, zktags);
			else buf.append(zktags);
		}
	
		out.write(buf.toString());
		out.write('\n');
	}
	/** Generates the ZK specific HTML tags.
	 * @return the buffer holding the HTML tags, or null if already generated.
	 */
	/*package*/ static String outZKHtmlTags(Desktop desktop) {
		final Execution exec = Executions.getCurrent();
		final String ATTR_ACTION = "zk_argAction";
		final String action = (String)exec.getAttribute(ATTR_ACTION);
		if (action == null)
			return null;

		final StringBuffer sb = new StringBuffer(512).append('\n')
			.append(ZkFns.outLangStyleSheets()).append('\n')
			.append(ZkFns.outLangJavaScripts(action)).append('\n');

		sb.append("<script type=\"text/javascript\">\nzkau.addDesktop(\"")
			.append(desktop.getId())
			.append("\");\n</script>\n");

		exec.removeAttribute(ATTR_ACTION); //turn off page.dsp's generation
		return sb.toString();
	}
}
