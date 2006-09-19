/* Body.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:50:07     2005, Created by tomyeh@potix.com
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
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.fn.ZkFns;
import org.zkoss.zhtml.impl.AbstractTag;

/**
 * The BODY tag.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
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

		final Execution exec = Executions.getCurrent();
		final String ATTR_RESPONSES = "zk_argResponses";
		final Collection responses = (Collection)exec.getAttribute(ATTR_RESPONSES);
		if (responses != null) {
			int j = buf.indexOf("</body>");
			if (j < 0) j = buf.length();
			buf.insert(j, "\n</script>\n")
			   .insert(j, ZkFns.outResponseJavaScripts(responses))
			   .insert(j, "\n<script type=\"text/javascript\">\n");

			exec.removeAttribute(ATTR_RESPONSES); //turn off page.dsp's generation
		}

		out.write(buf.toString());
	}
}
