package org.zkoss.zktest.test2;

import java.util.List;
import java.util.Random;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

public class B65_ZK_1766_Composer extends SelectorComposer<Component> {
	private static final long serialVersionUID = -6966844628906405777L;
	private static final boolean CLOSED = false;
	private static final boolean OPEN = true;

	DefaultTreeModel<TestObject> treeModel;

	@Wire
	private Tree myTree;

	public static int NUM_ROWS = 10;
	public static boolean ALEATORY_LEAFS = true;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if (!comp.getDesktop().isServerPushEnabled()) {
			comp.getDesktop().enableServerPush(true);
		}
		myTree.setItemRenderer(new TreeRendererTest());
		fill1();
	}

	@Listen("onClick = #invalidate")
	public void invalidate() {
		myTree.invalidate();
	}

	@Listen("onClick = #removeLast")
	public void removeLast() {
		List<TreeNode<TestObject>> children = treeModel.getRoot().getChildren();
		children.remove(children.size() - 1);
	}

	@Listen("onClick = #update2")
	public void update() {
		DefaultTreeNode<TestObject> root = (DefaultTreeNode<TestObject>) treeModel.getRoot();
		List<TreeNode<TestObject>> children = root.getChildren();
		TreeNode<TestObject> update1 = children.get(0);
		TreeNode<TestObject> update2 = children.get(1);

		update1.getData().col1 = newRandomInt(100);
		update2.getData().col2 = newRandomDouble();

		update1.setData(update1.getData());
		update2.setData(update2.getData());
	}

	@Listen("onClick = #updateAll")
	public void updateAll() {
		DefaultTreeNode<TestObject> root = (DefaultTreeNode<TestObject>) treeModel.getRoot();
		List<TreeNode<TestObject>> children = root.getChildren();
		for (TreeNode<TestObject> treeNode : children) {
			TestObject data = treeNode.getData();
			data.col1 = newRandomInt(100);
			data.col2 = newRandomDouble();
			treeNode.setData(data);
			List<TreeNode<TestObject>> children2 = treeNode.getChildren();
			if (children2 != null) {
				for (TreeNode<TestObject> treeNode2 : children2) {
					TestObject data2 = treeNode2.getData();
					data2.col1 = newRandomInt(100);
					data2.col2 = newRandomDouble();
					treeNode2.setData(data2);
				}
			}
		}

		TreeNode<TestObject> update1 = children.get(0);
		TreeNode<TestObject> update2 = children.get(1);

		update1.getData().col1 = newRandomInt(100);
		update2.getData().col2 = newRandomDouble();

		update1.setData(update1.getData());
		update2.setData(update2.getData());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Listen("onClick = #fill1")
	public void fill1() {
		treeModel = new DefaultTreeModel(new DefaultTreeNode(null, new DefaultTreeNode[] {}));
		myTree.setModel(treeModel);
		DefaultTreeNode rootNode = (DefaultTreeNode) treeModel.getRoot();

		rootNode.add(new DefaultTreeNode(testObject("Node 1", 1, false, CLOSED), new DefaultTreeNode[] {
			new DefaultTreeNode(testObject("Subnode 1-1", 2, true, CLOSED)),
			new DefaultTreeNode(testObject("Subnode 1-2", 2, true, CLOSED))
		}));
		rootNode.add(new DefaultTreeNode(testObject("Node 2", 1, false, OPEN), new DefaultTreeNode[] {
			new DefaultTreeNode(testObject("Subnode 2-1", 2, true, CLOSED)),
			new DefaultTreeNode(testObject("Subnode 2-2", 2, true, CLOSED))
		}));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Listen("onClick = #fill2")
	public void fill2() {
		treeModel = new DefaultTreeModel(new DefaultTreeNode(null, new DefaultTreeNode[] {}));
		myTree.setModel(treeModel);
		DefaultTreeNode rootNode = (DefaultTreeNode) treeModel.getRoot();

		rootNode.add(new DefaultTreeNode(testObject("Node 1", 1, false, CLOSED)));
		rootNode.add(new DefaultTreeNode(testObject("Node 2", 1, false, CLOSED), new DefaultTreeNode[] {
			new DefaultTreeNode(testObject("Subnode 2-1", 2, true, CLOSED)),
			new DefaultTreeNode(testObject("Subnode 2-2", 2, true, CLOSED))
		}));
	}

	@SuppressWarnings("unchecked")
	@Listen("onClick = #fillRandom")
	public void fillRandom() {
		treeModel = new DefaultTreeModel<TestObject>(new DefaultTreeNode<TestObject>(null, new DefaultTreeNode[] {}));
		myTree.setModel(treeModel);
		TreeNode<TestObject> rootNode = treeModel.getRoot();

		for (int i = 0; i < NUM_ROWS; i++) {
			TestObject test0 = testObject("Node " + i, 1, false, CLOSED);
			int iNumLeafs = newRandomInt(3);
			if (!ALEATORY_LEAFS || iNumLeafs > 0) {
				TestObject test1 = testObject("Subnode " + i + "-1", 2, true, CLOSED);
				if (!ALEATORY_LEAFS || iNumLeafs > 1) {
					TestObject test2 = testObject("Subnode " + i + "-2", 2, true, CLOSED);
					rootNode.add(new DefaultTreeNode<TestObject>(test0, new DefaultTreeNode[] {
						new DefaultTreeNode<TestObject>(test1),
						new DefaultTreeNode<TestObject>(test2)
					}));
				} else {
					rootNode.add(new DefaultTreeNode<TestObject>(test0, new DefaultTreeNode[] {
						new DefaultTreeNode<TestObject>(test1)
					}));
				}
			} else {
				rootNode.add(new DefaultTreeNode<TestObject>(test0));
			}
		}

	}

	private TestObject testObject(String name, int level, boolean leaf, boolean open) {
		TestObject test = new TestObject();
		test.col0 = name + " L" + level;
		test.col1 = new Integer(0);
		test.col2 = new Double(0);
		test.open = open;
		test.leaf = leaf;
		return test;
	}

	public static int newRandomInt(int n) {
		Random randomGenerator = new Random();
		return randomGenerator.nextInt(n);
	}

	public static double newRandomDouble() {
		double MEAN = 100.0f;
		double VARIANCE = 5.0f;
		Random randomGenerator = new Random();
		return MEAN + randomGenerator.nextGaussian() * VARIANCE;
	}

	public class TestObject {
		public String col0;
		public Integer col1;
		public Double col2;
		public boolean open;
		public boolean leaf;
	}

	public class TreeRendererTest implements TreeitemRenderer<DefaultTreeNode<TestObject>> {
		@SuppressWarnings({ "rawtypes", "unchecked" })
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
			Treecell t0 = new Treecell();
			t0.setLabel(row.col0);
			tr.appendChild(t0);
			Treecell t1 = new Treecell();
			Div t1Div = new Div();
			t1Div.appendChild(new Label(row.col1.toString()));
			if (row.col1.intValue() >= 50) {
				t1Div.setClass("green_class");
			} else if (row.col1.intValue() == 0) {
				t1Div.setClass("white_class");
			} else {
				t1Div.setClass("red_class");
			}
			t1.appendChild(t1Div);
			tr.appendChild(t1);

			Treecell t2 = new Treecell();
			Div t2Div = new Div();
			t2Div.appendChild(new Label(row.col2.toString()));
			if (row.col2.doubleValue() >= 100) {
				t2Div.setClass("green_class");
			} else if (row.col2.doubleValue() == 0) {
				t2Div.setClass("white_class");
			} else {
				t2Div.setClass("red_class");
			}
			t2.appendChild(t2Div);
			tr.appendChild(t2);

			if (!treeItem.isListenerAvailable(Events.ON_OPEN, true)) {
				treeItem.addEventListener(1000, Events.ON_OPEN, new EventListener<Event>() {
					public void onEvent(Event event) throws Exception {
						Treeitem ti = (Treeitem) event.getTarget();
						ti.setSelected(true);
						DefaultTreeNode<TestObject> dtn = (DefaultTreeNode<TestObject>) ti.getValue();
						TestObject row = (TestObject) dtn.getData();
						row.open = (((OpenEvent) event).isOpen());
					}
				});
			}

			treeItem.setOpen(row.open);

			// With this we ensure the row can be opened after its updated
			DefaultTreeModel<TestObject> model = (DefaultTreeModel) treeItem.getTree().getModel();
			if (row.open)
				model.addOpenObject(treeNode);
		}
	}
}
