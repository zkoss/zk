/* ITreeTest.java

	Purpose:

	Description:

	History:
		11:16 AM 2022/2/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

/**
 * Test for {@link ITree}
 * @author jumperchen
 */
public class ITreeTest extends StatelessTestBase {
	@Test
	public void withTree() {
		// check Richlet API case
		assertEquals(richlet(() -> ITree.of(ITreeitem.of("Item 1"))),
				zul(ITreeTest::newTree));

		// check Stateless file case
		assertEquals(composer(ITreeTest::newTree), zul(ITreeTest::newTree));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(ITreeTest::newTree, (ITree tree) ->
						tree.withRows(2)),
				zul(() -> {
					Tree tree = newTree();
					tree.setRows(2);
					return tree;
				})
		);
	}

	private static Tree newTree() {
		Tree tree = new Tree();
		tree.appendChild(new Treechildren());
		tree.getTreechildren().appendChild(new Treeitem("Item 1"));
		return tree;
	}
}
