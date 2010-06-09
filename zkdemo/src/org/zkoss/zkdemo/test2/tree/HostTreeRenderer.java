/* HostTreeRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 24, 2008 11:11:19 AM     2008, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2.tree;

import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

/**
 * @author Dennis.Chen
 *
 */
public class HostTreeRenderer implements TreeitemRenderer {


	public void render(Treeitem treeitem, Object data) throws Exception {
		Treerow row;
		if(treeitem.getTreerow()==null){// tree row not create yet.
			row = new Treerow();
			row.setParent(treeitem);
		}else{
			row = treeitem.getTreerow(); 
			row.getChildren().clear();
		}
		if(data instanceof HostTreeModel.FakeGroup){
			treeitem.getTreerow().appendChild(new Treecell(((HostTreeModel.FakeGroup)data).getName()));
		}else if(data instanceof HostTreeModel.FakeHost){
			treeitem.getTreerow().appendChild(new Treecell(((HostTreeModel.FakeHost)data).getName()));
		}else if(data instanceof HostTreeModel.FakeProcess){
			treeitem.getTreerow().appendChild(new Treecell(((HostTreeModel.FakeProcess)data).getName()));
		}
		
		//treeitem.setOpen(false);
	}

}
