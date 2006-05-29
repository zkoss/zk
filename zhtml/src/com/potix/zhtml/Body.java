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
package com.potix.zhtml;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.sys.PageCtrl;
import com.potix.zhtml.impl.AbstractTag;

/**
 * The BODY tag.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/29 04:27:46 $
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
}
