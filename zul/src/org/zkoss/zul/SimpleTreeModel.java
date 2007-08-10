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

import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * A simple implementation of {@link TreeModel}.
 *
 * @author Jeff Liu
 */
public class SimpleTreeModel extends AbstractTreeModel{
	
	/** Constructor.
	 *
	 * @param root - the root of tree
	
	 * However, it is not a good idea to modify <code>node</code>
	 * once it is passed to this method with live is true,
	 * since {@link Tree} is not smart enough to hanle it.
	 * @since 2.4.1
	 */
	public SimpleTreeModel(Object obj){
		super.setRoot(obj);
	}
	
	//-- TreeModel --//
	public int getChildCount(Object parent) {
		if(isLeaf(parent))
			return -1;
		else{
			ArrayList al = (ArrayList)parent;
			return al.size();
		}
	}
	
	//-- TreeModel --//
	public boolean isLeaf(Object node) {
		boolean isLeaf =!(node instanceof ArrayList);
		if(!isLeaf){
			return (((ArrayList)node).size() == 0);
		}
		return isLeaf;
	}
	
	//-- TreeModel --//
	public Object getChild(Object parent, int index) {
		ArrayList al = (ArrayList)parent;
		return al.get(index);
	}
	
	//-- AbstractTreeModel --//
	public Object getRoot() {
		return super.getRoot();
	}
	
	/**
	 * Modify the node which parent is <code>parent</code> with index <code>index</code> by
	 *  value
	 * @param parent The parent of node is modified
	 * @param index The index of node is modified
	 * @param value The new value of node if modified
	 */
	public void set(Object parent, int index, Object value)
	{
		ArrayList al = (ArrayList)parent;
		al.set(index, value);
		fireEvent(parent,index,TreeDataEvent.CONTENTS_CHANGED);
	}
	
	/**
	 * remove the node which parent is <code>parent</code> with index <code>index</code>
	 * @param parent The parent of node is removed
	 * @param index The index of node is removed
	 */
	public void remove(Object parent, int index){
		ArrayList al = (ArrayList)parent;	
		try{
			al.remove(index);
			fireEvent(parent,index,TreeDataEvent.NODE_REMOVED);
		}
		catch(Exception exp){
			throw new IndexOutOfBoundsException("Out of bound: "+index+" while size="+al.size());
		}
		
	}
	
	/**
	 * append a new node which parent is <code>parent</code>
	 * by new node <code>newNode</code>
	 * @param parent The parent of node is appended
	 * @param newNode New node which is appended
	 */
	public void add(Object parent, Object newNode){
		ArrayList al = (ArrayList)parent;
		al.add(newNode);
		fireEvent(parent,al.size()-1,TreeDataEvent.NODE_ADDED);
	}
	
	/**
	 * insert new node which parent is <code>parent</code> with index <code>index</code>
	 * by new node <code>newNode</code>
	 * @param parent The parent of node is inserted
	 * @param index The index of node is inserted
	 * @param newNode New node which is inserted
	 */
	public void insert(Object parent, int index, Object newNode){
		ArrayList al = (ArrayList)parent;
		al.add(index, newNode);
		System.out.println(al);
		fireEvent(parent,index,TreeDataEvent.NODE_ADDED);
	}
}

