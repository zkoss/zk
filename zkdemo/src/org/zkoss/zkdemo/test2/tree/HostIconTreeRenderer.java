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
public class HostIconTreeRenderer implements TreeitemRenderer {


	public void render(Treeitem treeitem, Object data) throws Exception {
		Treerow row;
		if(treeitem.getTreerow()==null){// tree row not create yet.
			row = new Treerow();
			row.setParent(treeitem);
		}else{
			row = treeitem.getTreerow(); 
			row.getChildren().clear();
		}
		if(data instanceof HostTreeModel.FakeRoot){
			treeitem.getTreerow().appendChild(new Treecell(((HostTreeModel.FakeRoot)data).getName()));
		}else if(data instanceof HostTreeModel.FakeGroup){
			treeitem.getTreerow().appendChild(new Treecell(((HostTreeModel.FakeGroup)data).getName()));
		}else if(data instanceof HostTreeModel.FakeHost){
			HostTreeModel.FakeHost host = ((HostTreeModel.FakeHost)data);
			Treecell cell = new Treecell(host.getName());
			//cell.setImage("/img/add.gif");
			cell.setId(host.getId());
			treeitem.getTreerow().appendChild(cell);
		}else if(data instanceof HostTreeModel.FakeProcess){
			HostTreeModel.FakeProcess process = ((HostTreeModel.FakeProcess)data);
			Treecell cell = new Treecell();
			Label fakelabel = new Label();
			fakelabel.setValue(process.getName());
			fakelabel.setContext("editPopup");
			fakelabel.setParent(cell);
//			if(process.getType().equals("A")){
//				cell.setImage("/img/caldrbtn.gif");
//			}else if(process.getType().equals("B")){
//				cell.setImage("/img/bandbtn.gif");
//			}else if(process.getType().equals("C")){
//				cell.setImage("/img/upload.gif");
//			}
			//cell.setId(process.getId());
			treeitem.getTreerow().appendChild(cell);
		}
		
		//treeitem.setOpen(false);// set open false to show correct icon.
	}

}
