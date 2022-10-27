/* Issue0086TreePagingComposer.java

	Purpose:

	Description:

	History:
		2:24 PM 2021/12/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.issues;

import java.util.ArrayList;
import java.util.Collections;

import org.zkoss.statelessex.state.ITreeController;
import org.zkoss.stateless.ui.BuildContext;
import org.zkoss.stateless.ui.StatelessComposer;
import org.zkoss.stateless.zpr.ITree;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

/**
 * @author jumperchen
 */
public class Issue0086TreePagingComposer  implements StatelessComposer<ITree> {
	public ITree build(BuildContext<ITree> ctx) {
		DefaultTreeNode root = new DefaultTreeNode("ROOT", new ArrayList());
		DefaultTreeModel stm = new DefaultTreeModel(root);
		DefaultTreeNode visibleRoot = new DefaultTreeNode("Visible ROOT", new ArrayList());
		root.add(visibleRoot);

		int count = 20;
		DefaultTreeNode node2 = new DefaultTreeNode("0", Collections.emptyList());
		for (int i = 0; i < count; i++) {
			node2.add(new DefaultTreeNode(i, Collections.emptyList()));
		}
		visibleRoot.add(node2);

		// add other nodes
		for (int i = 1; i < count; i++) {
			visibleRoot.add(new DefaultTreeNode(i, Collections.emptyList()));
		}
		for (int i = 0; i < count; i++) {
			root.add(new DefaultTreeNode(i, Collections.emptyList()));
		}

		int childSize = root.getChildCount();
		DefaultTreeNode lastNode = (DefaultTreeNode) root.getChildAt(childSize - 1);
		for (int i = 0; i < count; i++) {
			lastNode.add(new DefaultTreeNode(i, Collections.emptyList()));
		}
		return ITreeController.of(ctx.getOwner(), stm).build();
	}
}