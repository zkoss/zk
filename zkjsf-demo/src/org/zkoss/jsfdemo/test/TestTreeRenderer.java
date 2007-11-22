/* MyTreeRender.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/8/20 上午 11:29:55     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsfdemo.test;

import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Treecell;

/**
 * @author Dennis.Chen
 *
 */
public class TestTreeRenderer implements TreeitemRenderer{

	/* (non-Javadoc)
	 * @see org.zkoss.zul.TreeitemRenderer#render(org.zkoss.zul.Treeitem, java.lang.Object)
	 */
	public void render(Treeitem item, Object data) throws Exception {
		Treerow tr;
		item.setValue(data);
		if(item.getTreerow()==null){
			tr = new Treerow();
			tr.setParent(item);
		}else{
			tr = item.getTreerow(); 
			tr.getChildren().clear();
		}
		Treecell tc = new Treecell(data.toString());
		tc.setParent(tr);
		item.setOpen(false);
	}

}
