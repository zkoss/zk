package org.zkoss.zktest.test2;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;


public class B70_ZK_2547Composer extends GenericForwardComposer {
	
	DefaultTreeModel mymodelA;
	private Tree myTreeA;
	Window window;
	
	public static int NUM_ROWS=50;
	public static boolean ALEATORY_LEAFS=true;
	public static int UPDATE_MILIS=30;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
        if(!comp.getDesktop().isServerPushEnabled()){
        	comp.getDesktop().enableServerPush(true);
        }	
        window = (Window)Path.getComponent("//mypage/mywindow");

		myTreeA = (Tree) window.getFellow("myTreeA");
		mymodelA = new DefaultTreeModel(new DefaultTreeNode(null, new DefaultTreeNode[] {}));
		fillTree(myTreeA, mymodelA, "A");
	}

	private void fillTree(Tree myTree, DefaultTreeModel mymodel, String treeName) {
		myTree.setItemRenderer(new TreeRendererTest());
		DefaultTreeNode rootNode = (DefaultTreeNode)mymodel.getRoot();
		for (int i = 0; i < NUM_ROWS; i++) {
			TestObject test0 = new TestObject();
			test0.col0 = "Row "+treeName+" "+i;
			int iNumLeafs = 2;
			if (!ALEATORY_LEAFS || iNumLeafs > 0) {
				TestObject test1 = new TestObject();
				test1.col0 = "SubRow " + treeName + " " + i + " X";
				if (!ALEATORY_LEAFS || iNumLeafs > 1) {
					TestObject test2 = new TestObject();
					test2.col0 = "SubRow " + treeName + " " + i + " Y";
					rootNode.add(new DefaultTreeNode(test0, new DefaultTreeNode[] {
		    			new DefaultTreeNode(test1),
		    			new DefaultTreeNode(test2)
		    		}));
				} else {
					rootNode.add(new DefaultTreeNode(test0, new DefaultTreeNode[] {
			    			new DefaultTreeNode(test1)}));
				}
			} else {
				rootNode.add(new DefaultTreeNode(test0));
			}
		}
		myTree.setModel(mymodel);
	}	
	
	public static int newRandomInt(int n) {
		Random randomGenerator = new Random();
	    return randomGenerator.nextInt(n);
	}
}

class TestObject {
	public String  col0;
	public boolean open;
	public boolean leaf;
}

class TreeRendererTest implements TreeitemRenderer{

	private static Hashtable<String, Integer> totals = new Hashtable<String, Integer>() {{
	    put("A", 0);
	    put("B", 0);
	    put("C", 0);
	}};
	
	public void render(Treeitem treeItem, Object data, int id) throws Exception {

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
		TestObject row = (TestObject)treeNode.getData();

		Treecell t0=new Treecell();
		t0.setLabel(row.col0);


		tr.appendChild(t0);
		
	}
}