/* ITreecolRichlet.java

	Purpose:

	Description:

	History:
		Mon Feb 21 09:50:36 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ITree;
import org.zkoss.zephyr.zpr.ITreecol;
import org.zkoss.zephyr.zpr.ITreecols;
import org.zkoss.zephyrex.state.ITreeController;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.ITreecol} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treecol">ITreecol</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.ITreecol
 */
@RichletMapping("/data/itree/iTreecol")
public class ITreecolRichlet implements StatelessRichlet {

	@RichletMapping("/maxlength")
	public List<IComponent> maxlength() {
		ITreecols cols = ITreecols.of(ITreecol.of("tree column").withId("col")
				.withMaxlength(2));
		return Arrays.asList(
				IButton.of("change maxlength").withAction(this::changeMaxlength),
				ITree.DEFAULT.withTreecols(cols).withHeight("500px")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMaxlength() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("col"), new ITreecol.Updater().maxlength(3));
	}

	@RichletMapping("/sortAscendingAndDescending")
	public IComponent sortAscendingAndDescending() {
		ITreecols cols = ITreecols.of(ITreecol.of("column")
				.withSortAscending(genAscComparator())
				.withSortDescending(genDscComparator()));
		ITree tree = ITree.DEFAULT.withTreecols(cols).withHeight("500px");
		return ITreeController.of(tree, getTreeModel()).build();
	}

	@RichletMapping("/sortDirection")
	public IComponent sortDirection() {
		ITreecols cols = ITreecols.of(ITreecol.of("column")
				.withSortDescending(genDscComparator()).withSortDirection(ITreecol.SortDirection.DESCENDING));
		ITree tree = ITree.DEFAULT.withTreecols(cols).withAutosort(ITree.Autosort.ENABLE).withHeight("500px");
		return ITreeController.of(tree, getTreeModel()).build();
	}

	private Comparator genAscComparator() {
		return new Comparator() {
			public int compare(Object t1, Object t2) {
				return ((TreeNode) t1).getData().toString().compareTo(((TreeNode) t2).getData().toString());
			}
		};
	}

	private Comparator genDscComparator() {
		return new Comparator() {
			public int compare(Object t1, Object t2) {
				return ((TreeNode) t2).getData().toString().compareTo(((TreeNode) t1).getData().toString());
			}
		};
	}

	private DefaultTreeModel getTreeModel() {
		return new DefaultTreeModel(
			new DefaultTreeNode(null,
				new DefaultTreeNode[] {
					new DefaultTreeNode("item b"),
					new DefaultTreeNode("item a"),
					new DefaultTreeNode("item c"),
					new DefaultTreeNode("item d"),
					new DefaultTreeNode("item e"),
					new DefaultTreeNode("item f")
				}
			));
	}
}