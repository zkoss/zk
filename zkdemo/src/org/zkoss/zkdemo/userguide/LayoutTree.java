/* LayoutTree.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Oct 1, 2007 4:02:36 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.*;

/**
 * The Table-of-Content tree on the left.
 *
 * @author jumperchen
 */
public class LayoutTree extends Tree implements AfterCompose {
	public LayoutTree() {
	}
	public void onSelect() {
		Treeitem item = getSelectedItem();
		if (item != null) {
			Include inc = (Include)getSpaceOwner().getFellow("xcontents");
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
