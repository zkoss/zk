/* TocTree.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 28 19:02:37     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.*;

/**
 * The Table-of-Content tree on the left.
 *
 * @author tomyeh
 */
public class TocTree extends Tree implements AfterCompose {
	public TocTree() {
	}
	public void onSelect() {
		Treeitem item = getSelectedItem();
		if (item != null) {
			Include inc = (Include)Path.getComponent("/root/contents/xcontents");
			inc.setSrc((String)item.getValue());
		}
	}
	public void afterCompose() {
		final Execution exec = Executions.getCurrent();
		String id = exec.getParameter("id");
		Treeitem item = null;
		if (id != null) {
			try {
				item = (Treeitem)getSpaceOwner().getFellow(id);
			} catch (ComponentNotFoundException ex) { //ignore
			}
		}

		if (item == null)
			item = (Treeitem)getSpaceOwner().getFellow("s1");
		exec.setAttribute("contentSrc", (String)item.getValue());
			//so index.zul know which page to load based on the id parameter
		selectItem(item);
	}
}
