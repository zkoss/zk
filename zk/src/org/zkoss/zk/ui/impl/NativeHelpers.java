/* NativeHelpers.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 23 17:33:59     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.idom.Namespace;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.fn.ZkFns;

/**
 * Utilities for implementing {@link org.zkoss.zk.ui.ext.Native.Helper}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class NativeHelpers {
	/** Generates the attributes for the specified properties
	 * and namespaces.
	 *
	 * @param props a map of name and value pairs or null
	 * if no properties at all.
	 * Note: the value doesn't contain any EL expression.
	 * @param namespaces a list of {@link Namespace}
	 * to be generated, or null if not.
	 * Note: EL expressions is not allowed
	 */
	public static final
	void getAttributes(StringBuffer sb, Map props, Collection namespaces) {
		if (namespaces != null && !namespaces.isEmpty()) {
			for (Iterator it = namespaces.iterator(); it.hasNext();) {
				final Namespace ns = (Namespace)it.next();
				sb.append(" xmlns");
				if (ns.getPrefix().length() > 0)
					sb.append(':').append(ns.getPrefix());
				sb.append("=\"").append(ns.getURI()).append('"');
			}
		}

		if (props != null && !props.isEmpty()) {
			for (Iterator it = props.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				HTMLs.appendAttribute(sb,
					Objects.toString(me.getKey()),
					Objects.toString(me.getValue()));
			}
		}
	}

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
	 * @since 3.0.4
	 */
	public static String outZKHtmlTags() {
		final Execution exec = Executions.getCurrent();
		if (exec == null)
			return null;

		final String ATTR_ACTION = "zk_argAction";
		if (exec.getAttribute(ATTR_ACTION) == null)
			return null;

		final StringBuffer sb = new StringBuffer(512).append('\n')
			.append(ZkFns.outLangStyleSheets()).append('\n')
			.append(ZkFns.outLangJavaScripts(null)).append('\n');

		sb.append("<script type=\"text/javascript\">\nzkau.addDesktop(\"")
			.append(exec.getDesktop().getId())
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
