/* SimpleTreeNode.java

	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by Jeff Liu

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;

/**
 * 
 * The treenode for {@link SimpleTreeModel}
 * Note: It assumes the content is immutable
 * 
 * @author Jeff
 * @since ZK 3.0.0
 */
public class SimpleTreeNode<E> implements java.io.Serializable {

	private E _data;
	
	private List<SimpleTreeNode<E>> _children;
	
	/**
	 * Constructor
	 * @param data  data of the receiver
	 * @param children children of the receiver
	 * <br>
	 * Notice: Only <code>SimpleTreeNode</code> can be contained in The List <code>children</code>
	 */
	public SimpleTreeNode(E data, List<SimpleTreeNode<E>> children){
		_data = data;
		_children = children;
	}
	
	/**
	 * Return data of the receiver
	 * @return data of the receiver
	 */
	public E getData(){
		return _data;
	}
	
	/**
	 * Return children of the receiver
	 * @return children of the receiver
	 */
	public List<SimpleTreeNode<E>> getChildren(){
		return _children;
	}
	
	/**
	 * Return data.toString(). If data is null, return String "Data is null"
	 * @return data.toString(). If data is null, return String "Data is null"
	 */
	public String toString(){
		return (_data == null)?"Data is null":_data.toString();
	}
	
	/**
	 * Returns true if the receiver is a leaf.
	 * @return true if the receiver is a leaf.
	 */
	public boolean isLeaf(){
		return (_children.size() == 0);
	}
	
	/**
	 * Returns the child SimpleTreeNode at index childIndex.
	 * @return the child SimpleTreeNode at index childIndex.
	 */
	public SimpleTreeNode<E> getChildAt(int childIndex){
		return _children.get(childIndex);
	}
	
	/**
	 * Returns the number of children SimpleTreeNodes the receiver contains.
	 * @return the number of children SimpleTreeNodes the receiver contains.
	 */
	public int getChildCount(){
		return _children.size();
	}
}
