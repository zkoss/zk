package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;

public class B96_ZK_4793Test {
	@Test
	public void test() {
		Treechildren rootChildren = new Treechildren();
		Treeitem item_1 = new Treeitem("item 1");
		Treeitem item_2 = new Treeitem("item 2");
		Treeitem item_3 = new Treeitem("item 3");
		Treechildren item_1_children = new Treechildren();
		Treeitem item_11 = new Treeitem("item 1.1");
		Treechildren item_11_children = new Treechildren();
		Treeitem item_111 = new Treeitem("item 1.1.1");

		Tree tree = new Tree();
		tree.appendChild(rootChildren);
		rootChildren.appendChild(item_1);
		rootChildren.appendChild(item_2);
		rootChildren.appendChild(item_3);
		item_1.appendChild(item_1_children);
		item_1_children.appendChild(item_11);
		item_11.appendChild(item_11_children);
		item_11_children.appendChild(item_111);

		Assert.assertEquals(item_1, tree.renderItemByPath(new int[] {0}));
		Assert.assertEquals(item_2, tree.renderItemByPath(new int[] {1}));
		Assert.assertEquals(item_3, tree.renderItemByPath(new int[] {2}));
		Assert.assertEquals(item_11, tree.renderItemByPath(new int[] {0, 0}));
		Assert.assertEquals(item_111, tree.renderItemByPath(new int[] {0, 0, 0}));

		Assert.assertNull("null for incorrect path", tree.renderItemByPath(new int[] {-1}));
		Assert.assertNull("null for incorrect path", tree.renderItemByPath(new int[] {0, -1}));
		Assert.assertNull("null for incorrect path", tree.renderItemByPath(new int[] {0, 0, -1}));

		Assert.assertNull("null for incorrect path", tree.renderItemByPath(new int[] {4}));
		Assert.assertNull("null for incorrect path", tree.renderItemByPath(new int[] {0, 2}));
		Assert.assertNull("null for incorrect path", tree.renderItemByPath(new int[] {0, 0, 2}));
		Assert.assertNull("null for incorrect path", tree.renderItemByPath(new int[] {0, 0, 100}));

		//these 3 paths fail where the last index is just 1 greater than allowed
		Assert.assertNull("null for incorrect path", tree.renderItemByPath(new int[] {3}));
		Assert.assertNull("null for incorrect path", tree.renderItemByPath(new int[] {0, 0, 1}));
		Assert.assertNull("null for incorrect path", tree.renderItemByPath(new int[] {0, 2}));
	}
}