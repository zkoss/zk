/* inaryTreeModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by Jeff Liu
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.tree;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.ext.TreeSelectionModel;

/** 
 * A simple implementation of binary tree model by an arraylist
 *
 * @author Jeff Liu
 */
public class BinaryTreeModel extends AbstractTreeModel implements TreeSelectionModel {
	
	private ArrayList _tree =null;
	
	/**
	 * Constructor
	 * @param tree the list is contained all data of nodes.
	 */
	public BinaryTreeModel(List tree){
		super(tree.get(0));
		_tree = (ArrayList)tree;
	}
	
	//-- TreeModel --//
	public Object getChild(Object parent, int index) {
		int i = _tree.indexOf(parent) *2 +1 + index;
		if( i>= _tree.size())
			return null;
		else
			return _tree.get(i);
	}
	
	//-- TreeModel --//
	public int getChildCount(Object parent) {
		int count = 0;
		if(getChild(parent,0) != null)
			count++;
		if(getChild(parent,1) != null)
			count++;
		return count;
	}
	
	//-- TreeModel --//
	public boolean isLeaf(Object node) {
		return (getChildCount(node) == 0);
	}

	@Override
	public void setMultiple(boolean multiple) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isMultiple() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void addSelectionPath(int[] path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSelectionPaths(int[][] paths) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removeSelectionPath(int[] path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeSelectionPaths(int[][] paths) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPathSelected(int[] path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSelectionEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearSelection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getSelectionPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[][] getSelectionPaths() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSelectionCount() {
		return 0;
	}
}
