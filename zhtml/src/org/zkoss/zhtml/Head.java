/* Head.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:49:25     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.io.StringWriter;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.impl.NativeHelpers;
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

		addZkHtmlTags(buf, "head");

		out.write(buf.toString());
		out.write('\n');
	}
	/** Adds ZkFns.outZkHtmlTags if necessary.
	 * @param tag the tag name, such as "head" and "body"
	 */
	/*package*/ static void addZkHtmlTags(StringBuffer buf, String tag) {
		final String zktags = ZkFns.outZkHtmlTags();
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
