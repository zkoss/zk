/* B50_ZK_564_Composer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 21, 2011 11:14:29 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.event.TreeDataListener;

/**
 * @author jumperchen
 *
 */
public class B50_ZK_564_Composer extends GenericForwardComposer {
	private Tree tree;
	private Button add, insert;
	DefaultTreeNode root;
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		root = new DefaultTreeNode("", new DefaultTreeNode[0]);
		TreeModel model = new DefaultTreeModel(root);
		TreeDataListener listener = new TreeDataListener() {
			public void onChange(TreeDataEvent event) {
				switch(event.getType()) {
				case TreeDataEvent.INTERVAL_ADDED:
					alert("INTERVAL_ADDED");
					break;
				case TreeDataEvent.INTERVAL_REMOVED:
					alert("INTERVAL_REMOVED");
					break;
				}
			}
		};
		model.addTreeDataListener(listener);
		TreeitemRenderer renderer = new TreeitemRenderer() {
			public void render(Treeitem item, Object data) {
				item.setValue(data);
				item.setLabel("" + ((DefaultTreeNode) data).getData());
			}
		};
		tree.setModel(model);
		tree.setItemRenderer(renderer);
	}

	int i = 0;
	
	public void onClick$add(Event event) {
		Treeitem item = tree.getSelectedItem();
		DefaultTreeNode parent = item == null ? root : (DefaultTreeNode) item.getValue();
		if (parent.isLeaf()) {
			alert("Leaf");
			return;
		}
		String s = "TN-" + i++;
		DefaultTreeNode newNode = new DefaultTreeNode(s, new DefaultTreeNode[0]);
		parent.add(newNode);
	}
}
