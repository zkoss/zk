/* SimpleTreeitemRenderer.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Aug 15, 2011 6:33:59 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.tree;

import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

/**
 *
 * @author simonpai
 */
public class DirectoryTreeitemRenderer implements TreeitemRenderer {
	
	public void render(Treeitem treeItem, Object node, int index) throws Exception {
        final DirectoryTreeNode dtn = (DirectoryTreeNode) node;
        final PackageDataUnit pkgData = (PackageDataUnit) dtn.getData();
        treeItem.setOpen(dtn.isOpen());// Whether open the node
        Treerow treeRow = new Treerow();
        treeItem.appendChild(treeRow);
        treeRow.appendChild(new Treecell(pkgData.getPath()));
        treeRow.appendChild(new Treecell(pkgData.getDescription()));
        treeRow.appendChild(new Treecell(pkgData.getSize()));
	}
	
}
