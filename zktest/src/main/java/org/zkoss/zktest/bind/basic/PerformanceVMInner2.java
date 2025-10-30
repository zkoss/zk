package org.zkoss.zktest.bind.basic;

import java.util.Random;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

public class PerformanceVMInner2 {
	TreeModel _model;
	TreeNode _root;
	static int switchCount = 0;

	public TreeModel getModel() {
		if (_model == null) {
			_model = new DefaultTreeModel(getRoot());
		}
		
		return _model;
	}

	private TreeNode getRoot() {
		Random r = new Random();
		if (_root == null) {
			TreeNode[] nodes = new TreeNode[10];
			for (int i = 0; i < nodes.length; i++) {
				nodes[i] = new DefaultTreeNode(new TreePerformanceTestData("dataOne_" + (i)));
			}
//			System.out.println(">>>new nodes "+nodes.length);
			_root = new DefaultTreeNode(null, nodes);
		}
		return _root;
	}
	
	

	public int getSwitchCount() {
		return ++switchCount;
	}
	
	public class TreePerformanceTestData {
		
		int count;
		
		private String _dataOne;

		public TreePerformanceTestData(String dataOne) {
			_dataOne = dataOne;
		}

		public String getDataOne() {
			return _dataOne+":"+(++count);
		}

		public void setDataOne(String dataOne) {
			_dataOne = dataOne;
		}
	}
}
