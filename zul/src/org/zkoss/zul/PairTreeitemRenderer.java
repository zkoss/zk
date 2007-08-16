/* RowsTreeitemRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by Jeff Liu
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;

import org.zkoss.util.Pair;

public class PairTreeitemRenderer implements TreeitemRenderer {

	public void render(Treeitem item, Object data) throws Exception {
		//if the node is a branch
		String xValue = "Branch: ";
		String yValue = "";
		
		//if the node is a leaf
		if (data instanceof Pair) {
			Pair p = (Pair) data;
			xValue = p.x.toString();
			yValue = p.y.toString();
		}
		
		//Contruct treecells
		Treecell tcX = new Treecell(xValue);
		Treecell tcY = new Treecell(yValue);
		Treerow tr = null;
		/*
		 * Since only one treerow is allowed, if treerow is not null,
		 * append treecells to it. If treerow is null, contruct a new
		 * treerow and attach it to item.
		 */
		if(item.getTreerow()==null){
			tr = new Treerow();
			tr.setParent(item);
		}else{
			tr = item.getTreerow(); 
			tr.getChildren().clear();
		}
		//Attch treecells to treerow
		tcX.setParent(tr);
		tcY.setParent(tr);
		item.setOpen(false);
	}

}
