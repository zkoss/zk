/* TreeNode.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec 31 19:25:46 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul;

import java.util.List;

/**
 * Defines the requirements for an object that can be used as a
 * tree node in {@link SimpleTreeModel}.
 * 
 * @author tomyeh
 * @since 6.0.0
 */
public interface TreeNode<E> {
	/**
	 * Returns the child <code>TreeNode</code> at index 
	 * <code>childIndex</code>.
	 */
	public TreeNode<E> getChildAt(int childIndex);

	/**
	 * Return data of the receiver
	 */
	public E getData();

	/**
	 * Returns the number of children <code>TreeNode</code>s the receiver
	 * contains.
	 */
	public int getChildCount();

	/**
	 * Returns the parent <code>TreeNode</code> of the receiver.
	 */
//	public TreeNode<E> getParent();

	/**
	 * Returns the index of <code>node</code> in the receivers children.
	 * If the receiver does not contain <code>node</code>, -1 will be
	 * returned.
	 */
	public int getIndex(TreeNode<E> node);

	/**
	 * Returns true if the receiver is a leaf.
	 */
	public boolean isLeaf();
}
