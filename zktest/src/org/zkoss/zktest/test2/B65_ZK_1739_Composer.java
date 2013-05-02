package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

public class B65_ZK_1739_Composer extends GenericForwardComposer<Window> {
	private static final long serialVersionUID = 3698842561098482779L;

	private Tree myTreeA;
	private DefaultTreeModel<TestObject> mymodelA;
	private TestThread thread;
	int size = 5;
	
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		
		if(!comp.getDesktop().isServerPushEnabled()){
        	comp.getDesktop().enableServerPush(true);
        }
		
		mymodelA = new DefaultTreeModel<TestObject>(new DefaultTreeNode<TestObject>(null, new ArrayList<DefaultTreeNode<TestObject>>()));
		fillTree(myTreeA, mymodelA, "A");
		
		thread = new TestThread(myTreeA, mymodelA);
	}
	public void onClick$start() {
		thread.start();
	}
	public void onClick$toggle() {
		thread.stop();
	}
	
	private void fillTree(Tree myTree, DefaultTreeModel<TestObject> mymodel, String treeName) {
		myTree.setItemRenderer(new TreeRendererTest());
		myTree.setModel(mymodel);
		DefaultTreeNode<TestObject> rootNode = (DefaultTreeNode<TestObject>) mymodel.getRoot();
		Random rand = new Random();
		for (int i = 0; i < size; i++) {
			TestObject test = new TestObject();
			test.col0 = "Row " + treeName + " " + i;
			test.col1 = "0";
			TestObject test1 = new TestObject();
			test1.col0 = "SubRow " + treeName + " " + i + " X";
			test1.col1 = "0";
			TestObject test2 = new TestObject();
			test2.col0 = "SubRow " + treeName + " " + i + " Y";
			test2.col1 = "0";
			DefaultTreeNode<TestObject> n = new DefaultTreeNode<TestObject>(test, new ArrayList<DefaultTreeNode<TestObject>>());
			n.add(new DefaultTreeNode<TestObject>(test1));
			n.add(new DefaultTreeNode<TestObject>(test2));
			rootNode.add(n);
			if (rand.nextBoolean())
				mymodel.addOpenObject(n);
		}
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
			tr.appendChild(t2);
		}
	}

	class TestThread extends Thread implements Runnable, EventListener<Event> {
		Desktop desktop;
		Window window;
		Tree myTreeA;
		DefaultTreeModel<TestObject> mymodelA;

		private volatile boolean updating = true;
		
		public void toggleUpdates() {
			updating = !updating;
		}
		
		public TestThread(Tree tree, DefaultTreeModel<TestObject> model) {
			desktop = tree.getDesktop();
			myTreeA = tree;
			mymodelA = model;
		}
		
	    public void run() {
	    	boolean disconnect=false;
	    	while (!disconnect) {
	            try {
	            	if(updating) {
	            		if (desktop == null || !desktop.isAlive() || !desktop.isServerPushEnabled()) {
	            			desktop.enableServerPush(false);
	            			disconnect=true;;
	            		}
	            		
	            		int row=newRandomInt(size);
	            		int leaf=newRandomInt(3);
	            		String name="onCol1";
	            		String id;
	            		if (leaf==0) {
	            			id="Row A " + row;
	            		}
	            		else {
	            			id="SubRow A " + row;
	            			if (leaf==1) {id=id+" X";}
	            			else if (leaf==2) {id=id+" Y";}
	            		}
	            		Executions.schedule(desktop, this, new Event(name, null, id));
	            	}
	            	Thread.sleep(20);
				} catch(Exception e) {
					disconnect=true;
					System.out.println("ERROR:"+e.getMessage());
				}
	        }
	    }

//		@Override
		public void onEvent(Event arg0) throws Exception {
			if (arg0.getName().equals("onCol1")) {
				updateCol((String)arg0.getData(), 1);
			} else if (arg0.getName().equals("onCol2")) {
				updateCol((String)arg0.getData(), 2);
			}
		}
		
		public void updateCol(String id, int col){
		    DefaultTreeNode<TestObject> rootNode;
		    if (id.startsWith("Row A") || id.startsWith("SubRow A")) {
				rootNode=(DefaultTreeNode<TestObject>)mymodelA.getRoot();
		    } else {
		    	System.out.println("ERROR id="+id);
		    	return;
		    }
			Iterator<TreeNode<TestObject>> it = rootNode.getChildren().iterator();
			TestObject row;
			DefaultTreeNode<TestObject> node;
			while(it.hasNext()) {
				node= (DefaultTreeNode<TestObject>)it.next();
				row=(TestObject)node.getData();
				if (row.col0.equals(id)) {
					if (col==1) {
						row.col1="" + newRandomInt(100);
					}
					node.setData(row);
					break;
				} else if (id.startsWith("Sub"+row.col0)) {
					DefaultTreeNode<TestObject> nodeSub;
					TestObject rowSub;
					for (int index = 0; index < node.getChildCount(); index++) {
						nodeSub=(DefaultTreeNode<TestObject>)node.getChildAt(index);
						rowSub=(TestObject)nodeSub.getData();
						if (rowSub.col0.equals(id)) {
							if (col==1) {
								rowSub.col1=""+newRandomInt(100);
							}
						}
						nodeSub.setData(rowSub);
			        }
				}
			}
		}
		
		private int newRandomInt(int n) {
			Random randomGenerator = new Random();
		    return randomGenerator.nextInt(n);
		}
	}
	
	class TestObject {
		public String col0, col1;
	}
}
