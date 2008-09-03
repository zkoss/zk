/* Body.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:50:07     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
	public void setParent(Component parent) {
		final Page old = getPage();
		super.setParent(parent);
		fixDefaultParent(getPage(), old);
	}
	public void setPage(Page page) {
		final Page old = getPage();
		super.setPage(page);
		fixDefaultParent(page, old);
	}
	private void fixDefaultParent(Page page, Page old) {
		if (page != old) {
			if (old != null) {
				final PageCtrl oldc = (PageCtrl)old;
				if (oldc.getDefaultParent() == this)
					oldc.setDefaultParent(null);
			}
			if (page != null)
				((PageCtrl)page).setDefaultParent(this);
		}
	}

	//--Component-//
	public void redraw(java.io.Writer out) throws java.io.IOException {
		final StringWriter bufout = new StringWriter();
		super.redraw(bufout);
		final StringBuffer buf = bufout.getBuffer();

		final String zktags = NativeHelpers.outZKHtmlTags();
		if (zktags != null) {
			final int j = buf.indexOf("<body>");
			buf.insert(j>= 0 ? j + 6: 0, zktags);
		}
	
		out.write(buf.toString());
		out.write('\n');
	}
}
