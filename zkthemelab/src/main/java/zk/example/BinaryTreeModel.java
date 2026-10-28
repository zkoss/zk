/* BinaryTreeModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 2, 2010 9:53:06 AM , Created by Sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package zk.example;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

/**
 * @author Sam
 *
 */
public class BinaryTreeModel extends AbstractTreeModel {
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
}
