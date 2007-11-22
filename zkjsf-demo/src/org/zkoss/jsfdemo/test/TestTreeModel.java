/* MyTreeModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/8/20 上午 11:25:34     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsfdemo.test;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.AbstractTreeModel;

/**
 * @author Dennis.Chen
 *
 */
public class TestTreeModel extends AbstractTreeModel{

private ArrayList _tree =null;
	
	/**
	 * Contructor
	 * @param tree the list is contained all data of nodes.
	 */
	public TestTreeModel(List tree){
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
