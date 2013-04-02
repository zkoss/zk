package org.zkoss.zktest.bind.issue;

import java.util.Random;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

public class B01698TreePerformance {
	TreeModel _model;
	TreeNode _root;
	private int _count;

	public TreeModel getModel() {
		if (_model == null) {
			_model = new DefaultTreeModel(getRoot());
		}
		
		return _model;
	}

	private TreeNode getRoot() {
		Random r = new Random();
		if (_root == null) {
			TreeNode[] nodes = new TreeNode[1000];
			for (int i = 0; i < nodes.length; i++) {
				nodes[i] = new DefaultTreeNode(new TreePerformanceTestData("dataOne_" + (i), "dataTwo_"
						+ r.nextInt(), "dataThree_" + r.nextInt(), this));
			}
//			System.out.println(">>>new nodes "+nodes.length);
			_root = new DefaultTreeNode(null, nodes);
		}
		return _root;
	}

	public void increaseCount() {
		_count++;
	}

	public int getCount() {
		return _count;
	}

	@Command
	@NotifyChange("count")
	public void updateCount() {

	}

	long t1 = -1;
	long t2 = -1;
	
	public class TreePerformanceTestData {
		
		private String _dataOne;
		private String _dataTwo;
		private String _dataThree;
		private B01698TreePerformance _vm;

		public TreePerformanceTestData(String dataOne, String dataTwo, String dataThree, B01698TreePerformance vm) {
			_dataOne = dataOne;
			_dataTwo = dataTwo;
			_dataThree = dataThree;
			_vm = vm;
		}

		public String getDataOne() {
			_vm.increaseCount();
//			long x = 0;
//			t2 = System.currentTimeMillis();
//			x = t2-t1;
//			t1 = t2;
//
//			System.out.println(">>>get data one "+_dataOne+" "+x);
			return _dataOne;
		}

		public void setDataOne(String dataOne) {
			_dataOne = dataOne;
		}

		public String getDataTwo() {
			return _dataTwo;
		}

		public void setDataTwo(String dataTwo) {
			_dataTwo = dataTwo;
		}

		public String getDataThree() {
			return _dataThree;
		}

		public void setDataThree(String dataThree) {
			_dataThree = dataThree;
		}
	}

}
