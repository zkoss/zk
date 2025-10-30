package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

/**
 * @author Dennis Chen
 * 
 */
public class B00611 {
	TreeModel<TreeNode<String>> model;

	public B00611() {
		
		DefaultTreeNode<String> root = new DefaultTreeNode<String>("Root",new TreeNode[]{});
		
		for(int i=0;i<1;i++){
			TreeNode<String> ni = new DefaultTreeNode<String>("A"+i);
			root.add(ni);
		}
		
		model = new DefaultTreeModel<String>(root);
		
	}

	public TreeModel<TreeNode<String>> getModel() {
		return model;
	}
	
}
