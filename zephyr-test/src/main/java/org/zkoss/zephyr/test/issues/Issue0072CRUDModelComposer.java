/* Issue0072CRUDModelComposer.java

	Purpose:

	Description:

	History:
		12:22 PM 2021/11/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.issues;

import static org.zkoss.zephyr.action.ActionType.onClick;

import java.util.ArrayList;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.zpr.IAnyGroup;
import org.zkoss.zephyrex.state.ITreeController;
import org.zkoss.zephyr.ui.BuildContext;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessComposer;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IHlayout;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.ITextbox;
import org.zkoss.zephyr.zpr.ITree;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * @author jumperchen
 */
public class Issue0072CRUDModelComposer implements StatelessComposer<IVlayout> {
	ITreeController ITreeController;
	TestTreeModel tma;
	ArrayList mother = new ArrayList();
	ArrayList child1 = new ArrayList();

	public IVlayout build(BuildContext<IVlayout> ctx) {
		IVlayout<IAnyGroup> owner = ctx.getOwner();

		//Assign children to root "mother"
		mother.add("Davis");
		mother.add(child1);

		ArrayList child2 = new ArrayList();
		child2.add("test");
		mother.add(child2);

		tma = new TestTreeModel(mother);
		ITree tree = ITree.ofId("tree");
		ITreeController = ITreeController.of(tree, tma);
		return owner.withChildren(ITreeController.build(), IHlayout.of(ILabel.of("Insert Node:"),
				ITextbox.of("aaa").withId("text"),
				IButton.of("insert").withAction(this::insert),
				IButton.of("update the selected item").withAction(this::update),
				IButton.of("remove the selected item").withAction(this::remove),
				IButton.of("tree invalidate").withAction(
						onClick((UiAgent uiAgent) -> uiAgent.replaceWith(Locator.of(tree),
								ITreeController.build())))));
	}

	@Action(type = Events.ON_CLICK)
	public void insert(String text) {
		Object[] datas = {text};
		tma.insert(child1, 0, 0, datas);
	}

	@Action(type = Events.ON_CLICK)
	public void update(String text) {
		int[][] selectionPaths = tma.getSelectionPaths();
		if (selectionPaths == null || selectionPaths.length == 0) {
			Clients.alert("Select at least one item");
		} else {
			tma.update(selectionPaths[0], text);
		}
	}

	@Action(type = Events.ON_CLICK)
	public void remove() {
		int[][] selectionPaths = tma.getSelectionPaths();
		if (selectionPaths == null || selectionPaths.length == 0) {
			Clients.alert("Select at least one item");
		} else {
			for (int[] path : tma.getSelectionPaths()) {
				tma.remove(path);
			}
		}
	}

	class TestTreeModel extends org.zkoss.zul.AbstractTreeModel {
		public TestTreeModel(Object root) {
			super(root);
		}

		public int getChildCount(Object parent) {
			if (isLeaf(parent))
				return -1;
			else {
				ArrayList al = (ArrayList) parent;
				return al.size();
			}
		}

		public boolean isLeaf(Object node) {
			boolean isLeaf = !(node instanceof ArrayList);
			if (!isLeaf) {
				return (((ArrayList) node).size() == 0);
			}
			return isLeaf;
		}

		public Object getChild(Object parent, int index) {
			ArrayList al = (ArrayList) parent;
			return al.get(index);
		}

		public Object getRoot() {
			return super.getRoot();
		}

		public void insert(Object parent, int indexFrom, int indexTo, Object[] newNodes) throws IndexOutOfBoundsException {
			ArrayList al = (ArrayList) parent;
			for (int i = indexFrom; i <= indexTo; i++) {
				try {
					al.add(i, newNodes[i - indexFrom]);
				} catch (Exception exp) {
					throw new IndexOutOfBoundsException(
							"Out of bound: " + i + " while size=" + al.size());
				}
			}
			fireEvent(org.zkoss.zul.event.TreeDataEvent.INTERVAL_ADDED, getPath(parent), indexFrom, indexTo);

		}

		public void update(int[] path, Object data) throws IndexOutOfBoundsException {
			int[] parent = path.length == 1 ? new int[0] : new int[] {path[0]};
			int index = path[path.length - 1];
			ArrayList p = (ArrayList) getChild(parent);
			p.set(index, data);

			fireEvent(TreeDataEvent.CONTENTS_CHANGED, parent, index, index, path);
		}

		public void remove(int[] path) throws IndexOutOfBoundsException {
			int[] parent = path.length == 1 ? new int[0] : new int[] {path[0]};
			int index = path[path.length - 1];
			ArrayList p = (ArrayList) getChild(parent);
			p.remove(index);
			fireEvent(TreeDataEvent.INTERVAL_REMOVED, parent, index, index, path);
		}
	}
}