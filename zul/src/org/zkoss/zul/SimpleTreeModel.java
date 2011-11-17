/* SimpleTreeModel.java

	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by Jeff Liu

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * @deprecated As of release 5.0.6, replaced with {@link DefaultTreeModel}.
 * A simple implementation of {@link TreeModel}.
 * Note: It assumes the content is immutable.
 *
 * @author Jeff Liu
 * @since 3.0.0
 */
public class SimpleTreeModel extends AbstractTreeModel {
	/**
	 * @deprecated As of release 5.0.6, replaced with {@link DefaultTreeModel}.
	 * Constructor
	 * @param root - the root of tree 
	 */
	public SimpleTreeModel(SimpleTreeNode root) {
		super(root);
	}
	
	//@Override
	public Object getChild(Object parent, int index) {
		return ((SimpleTreeNode)parent).getChildAt(index);
	}
	
	//@Override
	public int getChildCount(Object parent) {
		return ((SimpleTreeNode)parent).getChildCount();
	}

	//@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((SimpleTreeNode)parent).getIndex(child);
	}
	
	//@Override
	public boolean isLeaf(Object node) {
		if(node instanceof SimpleTreeNode){
			SimpleTreeNode n = (SimpleTreeNode)node;
			return n.isLeaf();
		}
		return true;
	}
}
