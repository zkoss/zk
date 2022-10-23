/* Issue0083TreeSortComposer.java

	Purpose:

	Description:

	History:
		3:24 PM 2021/12/13, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.issues;

import java.util.ArrayList;
import java.util.Collections;

import org.zkoss.zephyrex.state.ITreeController;
import org.zkoss.zephyr.ui.BuildContext;
import org.zkoss.zephyr.ui.StatelessComposer;
import org.zkoss.zephyr.zpr.ITree;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

/**
 * @author jumperchen
 */
public class Issue0083TreeSortComposer implements StatelessComposer<ITree> {
	public ITree build(BuildContext<ITree> ctx) {
		DefaultTreeNode root = new DefaultTreeNode("ROOT", new ArrayList());
		DefaultTreeModel stm = new DefaultTreeModel(root);
		DefaultTreeNode visibleRoot = new DefaultTreeNode("Visible ROOT", new ArrayList());
		root.add(visibleRoot);

		// add a tree node which has 2000 children
		DefaultTreeNode node2 = new DefaultTreeNode("0", Collections.emptyList());
		for (int i = 0; i < 10; i++) {
			node2.add(new DefaultTreeNode(i, Collections.emptyList()));
		}
		visibleRoot.add(node2);

		// add other nodes
		for (int i = 1; i < 10; i++) {
			visibleRoot.add(new DefaultTreeNode(i, Collections.emptyList()));
		}
		for (int i = 0; i < 10; i++) {
			root.add(new DefaultTreeNode(Integer.toHexString(i), Collections.emptyList()));
		}

		int childSize = root.getChildCount();
		DefaultTreeNode lastNode = (DefaultTreeNode) root.getChildAt(childSize - 1);
		for (int i = 0; i < 10; i++) {
			lastNode.add(new DefaultTreeNode(i, Collections.emptyList()));
		}
		return ITreeController.of(ctx.getOwner(), stm).build();
	}
}
