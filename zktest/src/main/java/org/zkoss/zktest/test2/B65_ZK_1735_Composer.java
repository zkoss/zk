package org.zkoss.zktest.test2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

public class B65_ZK_1735_Composer extends SelectorComposer<Component> {

	DefaultTreeModel mymodelA;
	private static int count = 0; 

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Tree myTreeA = (Tree) comp.getFellow("myTreeA");
		mymodelA = new DefaultTreeModel(new DefaultTreeNode(null,
				new DefaultTreeNode[] {}));
		fillTree(myTreeA, mymodelA, "A");

	}

	private void fillTree(Tree myTree, DefaultTreeModel mymodel, String treeName) {
		myTree.setItemRenderer(new TreeitemRenderer() {
			public void render(Treeitem treeItem, Object data, int id)
					throws Exception {

				DefaultTreeNode treeNode = (DefaultTreeNode) data;
				treeItem.setValue(treeNode);
				Treerow tr;
				if (treeItem.getTreerow() == null) {
					tr = new Treerow();
					tr.setParent(treeItem);
				} else {
					tr = treeItem.getTreerow();
					tr.getChildren().clear();
				}

				Map row = (Map) treeNode.getData();

				Treecell t0 = new Treecell();
				t0.setLabel(row.get("id").toString());
				tr.appendChild(t0);

				Treecell t1 = new Treecell();
				t1.setLabel(row.get("val").toString());
				tr.appendChild(t1);

			}

		});

		myTree.setModel(mymodel);

		DefaultTreeNode rootNode = (DefaultTreeNode) mymodel.getRoot();
		for (int i = 0; i < 10; i++) {
			Map test = new HashMap();
			test.put("id", "Row " + treeName + " " + i); 
			test.put("val", new Integer(0));
			Map test1 = new HashMap();
			test1.put("id","SubRow " + treeName + " " + i + " X");
			test1.put("val", new Integer(0));
			rootNode.add(new DefaultTreeNode(test,
					new DefaultTreeNode[] { new DefaultTreeNode(test1) }));
		}
	}

	@Listen("onClick=treerow")
	public void onCol1(Event event) {
		DefaultTreeNode rootNode;
		
		String id = ((Treecell) event.getTarget().getChildren().get(0))
				.getLabel();
		rootNode = (DefaultTreeNode) mymodelA.getRoot();
		Iterator it = rootNode.getChildren().iterator();
		Map row;
		DefaultTreeNode node;

		while (it.hasNext()) {
			node = (DefaultTreeNode) it.next();
			row = (Map) node.getData();
			if (row.get("id").equals(id)) {
				row.put("val", ++count);
				node.setData(row);
				break;
			}
		}
	}
}
