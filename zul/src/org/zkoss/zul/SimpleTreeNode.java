/* SimpleTreeNode.java

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

import java.util.List;
import org.zkoss.lang.Objects;

/**
 * @deprecated As of release 5.0.6, replaced with {@link DefaultTreeNode},
 * which is mutable.
 *
 * A simple implementation of {@link TreeNode} to be used with
 * {@link SimpleTreeModel}.
 * Note: It assumes the content is immutable.
 * 
 * @author Jeff
 * @since 3.0.0
 */
public class SimpleTreeNode implements java.io.Serializable {
	private Object _data;
	private List _children;
	
	/**
	 * Constructor
	 * @param data  data of the receiver
	 * @param children children of the receiver.
	 * It must be a list of {@link SimpleTreeNode}.
	 */
	public SimpleTreeNode(Object data, List children){
		_data = data;
		_children = children;
	}
	
	/**
	 * Return data of the receiver
	 * @return data of the receiver
	 */
	public Object getData(){
		return _data;
	}
	
	/**
	 * Return children of the receiver
	 * @return children of the receiver
	 */
	public List getChildren(){
		return _children;
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
	public Object getChildAt(int childIndex){
		return _children.get(childIndex);
	}
	
	/**
	 * Returns the number of children SimpleTreeNodes the receiver contains.
	 * @return the number of children SimpleTreeNodes the receiver contains.
	 */
	public int getChildCount(){
		return _children.size();
	}
	/** Returns the index of the given child, or -1 if the child does not belong
	 * to this.
	 * @since 5.0.6
	 */
	public int getIndex(Object child) {
		return _children.indexOf(child);
	}

	//Object//
	public String toString(){
		return Objects.toString(_data);
	}
}
