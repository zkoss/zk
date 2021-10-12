package org.zkoss.zktest.test2;

import java.util.Iterator;
import java.util.Random;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

public class B65_ZK_1726_Composer extends GenericForwardComposer<Tabbox> {
	private static final long serialVersionUID = 8214580190889099905L;

	private Tabbox myTab;
	private Tree myTreeA;
	private DefaultTreeModel<TestObject> mymodelA;

	public void doAfterCompose(Tabbox comp) throws Exception {
		super.doAfterCompose(comp);
		mymodelA = new DefaultTreeModel<TestObject>(new DefaultTreeNode<TestObject>(null, new DefaultTreeNode[] {}));
		fillTree(myTreeA, mymodelA, "A");
	}

	private void fillTree(Tree myTree, DefaultTreeModel<TestObject> mymodel, String treeName) {
		myTree.setItemRenderer(new TreeRendererTest());
		myTree.setModel(mymodel);
		DefaultTreeNode<TestObject> rootNode = (DefaultTreeNode<TestObject>) mymodel.getRoot();
		for (int i = 0; i < 10; i++) {
			TestObject test = new TestObject();
			test.col0 = "Row " + treeName + " " + i;
			test.col1 = "0";
			TestObject test1 = new TestObject();
			test1.col0 = "SubRow " + treeName + " " + i + " X";
			test1.col1 = "0";
			TestObject test2 = new TestObject();
			test2.col0 = "SubRow " + treeName + " " + i + " Y";
			test2.col1 = "0";
			rootNode.add(new DefaultTreeNode<TestObject>(test, new DefaultTreeNode[] {new DefaultTreeNode(test1), new DefaultTreeNode(test2)}));
		}
	}

	public void onCol1(Event event) {
		updateCol(event, 1);
	}

	public void updateCol(Event event, int col) {
		DefaultTreeNode<TestObject> rootNode;
		String id = (String) event.getData();
		if (id.startsWith("Row A") || id.startsWith("SubRow A")) {
			rootNode = (DefaultTreeNode<TestObject>) mymodelA.getRoot();
		} else {
			System.out.println("ERROR id=" + id);
			return;
		}
		Iterator<TreeNode<TestObject>> it = rootNode.getChildren().iterator();
		TestObject row;
		DefaultTreeNode<TestObject> node;
		while (it.hasNext()) {
			node = (DefaultTreeNode<TestObject>) it.next();
			row = (TestObject) node.getData();
			if (row.col0.equals(id)) {
				if (col == 1) {
					row.col1 = "" + newRandomInt();
				}
				node.setData(row);
				break;
			} else if (id.startsWith("Sub" + row.col0)) {
				DefaultTreeNode<TestObject> nodeSub;
				TestObject rowSub;
				for (int index = 0; index < node.getChildCount(); index++) {
					nodeSub = (DefaultTreeNode<TestObject>) node.getChildAt(index);
					rowSub = (TestObject) nodeSub.getData();
					if (rowSub.col0.equals(id)) {
						if (col == 1) {
							rowSub.col1 = "" + newRandomInt();
						}
					}
					nodeSub.setData(rowSub);
				}
			}
		}
	}

	private int newRandomInt() {
		Random randomGenerator = new Random();
		return randomGenerator.nextInt(100);
	}

	class TreeRendererTest implements TreeitemRenderer<DefaultTreeNode<TestObject>> {
		public void render(Treeitem treeItem, DefaultTreeNode<TestObject> treeNode, int id) throws Exception {
			treeItem.setValue(treeNode);
			Treerow tr;
			if (treeItem.getTreerow() == null) {
				tr = new Treerow();
				tr.setParent(treeItem);
			} else {
				tr = treeItem.getTreerow();
				tr.getChildren().clear();
			}

			TestObject row = (TestObject) treeNode.getData();

			final Treecell t1 = new Treecell();
			t1.setLabel(row.col0);
			tr.appendChild(t1);

			Treecell t2 = new Treecell();
			t2.setLabel(row.col1);
			if (!t2.isListenerAvailable(Events.ON_CLICK, true)) {
				t2.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					public void onEvent(Event event) throws Exception {
						Events.postEvent(new Event("onCol1", myTab, t1.getLabel()));
					}
				});
			}
			tr.appendChild(t2);
		}
	}

	class TestObject {
		public String col0, col1;
	}
}
