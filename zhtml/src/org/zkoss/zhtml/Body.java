/* Body.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:50:07     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.util.Collection;
import java.io.StringWriter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.impl.NativeHelpers;
import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.fn.ZkFns;

/**
 * The BODY tag.
 *
 * @author tomyeh
 */
public class Body extends AbstractTag {
	public Body() {
		super("body");
	}

	//-- super --//
	public void onPageAttached(Page newpage, Page oldpage) {
		fixDefaultParent(newpage, oldpage);
	}
	public void onPageDetached(Page page) {
		fixDefaultParent(null, page);
	}
	private void fixDefaultParent(Page page, Page old) {
		if (old != null) {
			final PageCtrl oldc = (PageCtrl)old;
			if (oldc.getDefaultParent() == this)
				oldc.setDefaultParent(null);
		}
		if (page != null)
			((PageCtrl)page).setDefaultParent(this);
	}

	//--Component-//
	public void redraw(java.io.Writer out) throws java.io.IOException {
		final StringWriter bufout = new StringWriter();
		super.redraw(bufout);
		final StringBuffer buf = bufout.getBuffer();

		Head.addZkHtmlTags(buf, "body");
		final String msg = ZkFns.outHtmlUnavailable(getPage());
		if (msg != null && msg.length() > 0) {
			final int j = buf.lastIndexOf("</body>");
			if (j >= 0) buf.insert(j, msg);
			else buf.append(msg);
		}

		out.write(buf.toString());
		out.write('\n');
	}
}
