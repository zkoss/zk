/* SimpleTreeModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by Jeff Liu
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.event.TreeDataEvent;

/** 
 * A simple implementation of binary tree model by an arraylist
 *
 * @author Jeff Liu
 */
public class BinaryTreeModel extends AbstractTreeModel{
	
	private ArrayList _tree =null;
	
	/**
	 * Contructir
	 * @param tree the list is contained all data of nodes.
	 */
	public BinaryTreeModel(List tree){
		_tree = (ArrayList)tree;
		super.setRoot(_tree.get(0));
	}
	
	//-- TreeModel--//
	public Object getChild(Object parent, int index) {
		int i = _tree.indexOf(parent) *2 +1 + index;
		if( i>= _tree.size())
			return null;
		else
			return _tree.get(i);
	}
	
	//-- TreeModel--//
	public int getChildCount(Object parent) {
		int count = 0;
		if(getChild(parent,0) != null)
			count++;
		if(getChild(parent,1) != null)
			count++;
		return count;
	}
	
	//-- TreeModel--//
	public boolean isLeaf(Object node) {
		return (getChildCount(node) == 0);
	}
	
	/**
	 * Add the new node to the end of list and last node of tree
	 * @param newNode new node to be added
	 */
	public void add(Object newNode)
	{
		_tree.add(newNode);
		int parentIndex = (_tree.indexOf(newNode)-1)/2;
		int i = (_tree.indexOf(newNode)) %2;
		if(i==0)
			i=1;
		else
			i=0;
		int[] indexes = {i};
		fireEvent(_tree.get(parentIndex),indexes,TreeDataEvent.NODE_ADDED);
	}

}
