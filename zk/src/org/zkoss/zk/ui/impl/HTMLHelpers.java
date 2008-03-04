/* HTMLHelpers.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Mar  4 19:17:39     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Collection;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.fn.ZkFns;

/**
 * Utilities to generate special HTML tags for XHTML page.
 *
 * <p>Refer to org.zkoss.zhtml.Head and page.dsp for examples.
 *
 * @author tomyeh
 * @since 3.0.4
 */
public class HTMLHelpers {
	/** Generates and returns the ZK specific HTML tags for
	 * a desktop.
	 *
	 * <p>For each desktop, we have to generate a set of HTML tags
	 * to load ZK Client engine, style sheets and so on.
	 * For ZUL pages, it is generated automatically by page.dsp.
	 * However, for ZHTML pages, we have to generate these tags
	 * with special component such as org.zkoss.zhtml.Head, such that
	 * the result HTML page is legal.
	 *
	 * @return the string holding the HTML tags, or null if already generated.
	 */
	public static String outZKHtmlTags(Desktop desktop) {
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

		final String ATTR_RESPONSES = "zk_argResponses";
		final Collection responses = (Collection)exec.getAttribute(ATTR_RESPONSES);
		if (responses != null && !responses.isEmpty())
			sb.append("\n<script type=\"text/javascript\">\n")
				.append(ZkFns.outResponseJavaScripts(responses))
				.append("\n</script>\n");
		exec.removeAttribute(ATTR_RESPONSES);

		exec.removeAttribute(ATTR_ACTION); //turn off page.dsp's generation
		return sb.toString();
	}
}
