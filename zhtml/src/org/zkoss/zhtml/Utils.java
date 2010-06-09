/* Utils.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  3 19:10:51 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zhtml;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.HtmlPageRenders;

/**
 * Utilities.
 * @author tomyeh
 * @since 5.0.3
 */
/*package*/ class Utils {
	/** Adds HtmlPageRenders.outHeaderZkTags if necessary.
	 * @param tag the tag name, such as "head", "body" and "html"
	 */
	/*package*/ static void
	addHeaderZkTags(Execution exec, Page page, StringBuffer buf, String tag) {
		if (HtmlPageRenders.isDirectContent(exec)) {
			final String zktags = HtmlPageRenders.outHeaderZkTags(exec, page);
			if (zktags != null && zktags.length() > 0) {
				int j = buf.indexOf("<" + tag);
				if (j >= 0) {
					j += tag.length() + 1;
					for (int len = buf.length(); j < len; ++j) {
						if (buf.charAt(j) == '>') {
							buf.insert(j + 1, zktags);
							return; //done
						}
					}
				}
				buf.append(zktags);
			}
		}
	}
	/** Adds both headers and other ZK-related tags.
	 * @param tag the tag name, such as "head", "body" and "html"
	 */
	/*package*/ static void
	addAllZkTags(Execution exec, Page page, StringBuffer buf, String tag) {
		addHeaderZkTags(exec, page, buf, tag);

		final String msg = HtmlPageRenders.outUnavailable(exec);
		if (msg != null && msg.length() > 0) {
			final int j = buf.lastIndexOf("</" + tag + '>');
			if (j >= 0) buf.insert(j, msg);
			else buf.append(msg);
		}
	}
}
