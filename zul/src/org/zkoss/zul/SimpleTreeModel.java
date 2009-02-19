/* SimpleTreeModel.java

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
package org.zkoss.zul;

/**
 * A simple implementation of {@link TreeModel}.
 * Note: It assumes the content is immutable.
 *
 * @author Jeff Liu
 * @since 3.0.0
 */
public class SimpleTreeModel extends AbstractTreeModel {
	
	/**
	 * Constructor
	 * @param root - the root of tree 
	 */
	public SimpleTreeModel(SimpleTreeNode root) {
		super(root);
	}
	
	//--TreeModel--//
	public Object getChild(Object parent, int index) {
		SimpleTreeNode node = (SimpleTreeNode)parent;
		return node.getChildAt(index);
	}
	
	//--TreeModel--//
	public int getChildCount(Object parent) {
		SimpleTreeNode node = (SimpleTreeNode)parent;
		return node.getChildCount();
	}
	
	//--TreeModel--//
	public boolean isLeaf(Object node) {
		if(node instanceof SimpleTreeNode){
			SimpleTreeNode node_ = (SimpleTreeNode)node;
			return node_.isLeaf();
		}else{
			return true;
		}
	}

}
