package org.zkoss.zktest.test2.B100_ZK_5535;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
public class Bug {
	private BugOasiListBoxLayoutModel gridTree=new BugOasiListBoxLayoutModel();
	public BugOasiListBoxLayoutModel getGridTree() {
		return gridTree;
	}
	public void setGridTree(BugOasiListBoxLayoutModel gridTree) {
		this.gridTree = gridTree;
	}
	public Bug()
    {
    }
	public void loadDati()
	{
		BugOasiTreeNode<BugFormModel> nodeParent=this.loadDati(null);
		for (int i=0;i<2000;i++)
		    nodeParent=this.loadDati(nodeParent);
		this.gridTree.setLeafNodes();
	}
	public BugOasiTreeNode<BugFormModel> loadDati(
			BugOasiTreeNode<BugFormModel> parent)
	{
		BugFormModel row=null;
		/* --------------------------------------- */
		/* Popolo grid tree                        */
		/* --------------------------------------- */
		BugOasiTreeNode<BugFormModel> nodeParent;
		BugOasiTreeNode<BugFormModel> node;
		/* ------------------------- */
		/* Creo nodo di root         */
		/* ------------------------- */
		nodeParent=gridTree.addTreeNode(parent,true);
		row=nodeParent.getData();
		row.get("coarfo").setStringVal("1");
		row.get("descri").setStringVal("Nodo root");
		nodeParent=gridTree.addTreeNode(true);
		row=nodeParent.getData();
		row.get("coarfo").setStringVal("1");
		row.get("descri").setStringVal("Nodo root2");
		nodeParent=gridTree.addTreeNode(true);
		row=nodeParent.getData();
		row.get("coarfo").setStringVal("1");
		row.get("descri").setStringVal("Nodo root3");
		nodeParent=gridTree.addTreeNode(true);
		row=nodeParent.getData();
		row.get("coarfo").setStringVal("1");
		row.get("descri").setStringVal("Nodo root4");
		nodeParent=gridTree.addTreeNode(true);
		row=nodeParent.getData();
		row.get("coarfo").setStringVal("1");
		row.get("descri").setStringVal("Nodo root5");
		nodeParent=gridTree.addTreeNode(true);
		row=nodeParent.getData();
		row.get("coarfo").setStringVal("1");
		row.get("descri").setStringVal("Nodo root6");
		/* ---------------------------------- */
		/* Creo primo nodo figlio senza figli */
		/* ---------------------------------- */
		node=gridTree.addTreeNode(nodeParent);
		node.setLeaf(true);
		row=node.getData();
		row.get("coarfo").setStringVal("2");
		row.get("descri").setStringVal("nodo figlio 1");
		/* ---------------------------------- */
		/* Creo secondo nodo figlio con figli */
		/* ---------------------------------- */
		node=gridTree.addTreeNode(nodeParent,false);
		row=node.getData();
		row.get("coarfo").setStringVal("3");
		row.get("descri").setStringVal("nodo figlio 2");
		/* ---------------------------------- */
		/* Creo figli secondo nodo figlio     */
		/* ---------------------------------- */
		nodeParent=node;
		node=gridTree.addTreeNode(nodeParent,true);
		row=node.getData();
		row.get("coarfo").setStringVal("4");
		row.get("descri").setStringVal("primo figlio del cocondo figlio");
		/* ---------------------------------- */
		/* Creo secondo nodo figlio con figli */
		/* ---------------------------------- */
		node=gridTree.addTreeNode(nodeParent,true);
		row=node.getData();
		row.get("coarfo").setStringVal("6");
		row.get("descri").setStringVal("secondo figlio del cocondo figlio");
		/* ---------------------------------- */
		/* Creo figlio in mezzo               */
		/* ---------------------------------- */
		node=gridTree.addTreeNode(nodeParent,true,1);
		row=node.getData();
		row.get("coarfo").setStringVal("5");
		row.get("descri").setStringVal("creato figlio in mezzo");
		
		return nodeParent;
	}
	@Command
	public void showData()
	{
		this.loadDati();
		BindUtils.postNotifyChange(null, null, this, "gridTree");
	}
	@Command
	public void clearTree()
	{
		this.gridTree.clearTree();
		BindUtils.postNotifyChange(null, null, this, "gridTree");
	}
}

