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
package org.zkoss.zkdemo.test2.tree;

import java.util.ArrayList;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * A simple implementation of {@link TreeModel}.
 *
 * @author Jeff Liu
 */
public class TreeModelA extends AbstractTreeModel{
	
	/** Constructor.
	 *
	 * @param root - the root of tree
	 * 
	 */
	public TreeModelA(Object root){
		super(root);
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
	 * Modify the nodes which parent is <code>parent</code> with indexes <code>indexes</code> by
	 *  values
	 * @param parent The parent of nodes are modified
	 * @param indexFrom the lower index of the change range
	 * @param indexTo the upper index of the change range
	 * @param values The new values of nodes are modified
	 * @throws IndexOutOfBoundsException - indexFrom < 0 or indexTo > number of parent's children
	 */
	public void set(Object parent, int indexFrom, int indexTo, Object[] values) throws IndexOutOfBoundsException{
		ArrayList al = (ArrayList)parent;
		for(int i=indexFrom; i<=indexTo;i++){
			try{
				al.set(i, values[i-indexFrom]);
			}catch(Exception exp){
				throw new IndexOutOfBoundsException("Out of bound: "+i+" while size="+al.size());
			}
		}
		fireEvent(parent,indexFrom,indexTo,TreeDataEvent.CONTENTS_CHANGED);
	}
	
	/**
	 * remove the nodes which parent is <code>parent</code> with indexes <code>indexes</code>
	 * @param parent The parent of nodes are removed
	 * @param indexFrom the lower index of the change range
	 * @param indexTo the upper index of the change range
	 * @throws IndexOutOfBoundsException - indexFrom < 0 or indexTo > number of parent's children
	 */
	public void remove(Object parent, int indexFrom, int indexTo) throws IndexOutOfBoundsException{
		ArrayList al = (ArrayList)parent;	
		for(int i=indexTo; i>=indexFrom;i--)
		try{
			al.remove(i);
		}catch(Exception exp){
			throw new IndexOutOfBoundsException("Out of bound: "+i+" while size="+al.size());
		}
		fireEvent(parent,indexFrom,indexTo,TreeDataEvent.INTERVAL_REMOVED);
		
	}
	
	/**
	 * append new nodes which parent is <code>parent</code>
	 * by new nodes <code>newNodes</code>
	 * @param parent The parent of nodes are appended
	 * @param newNodes New nodes which are appended
	 */
	public void add(Object parent, Object[] newNodes){
		ArrayList al = (ArrayList)parent;
		int indexFrom = al.size();
		int indexTo = al.size()+newNodes.length-1;
		for(int i=0; i<newNodes.length;i++)
			al.add(newNodes[i]);
		fireEvent(parent,indexFrom,indexTo,TreeDataEvent.INTERVAL_ADDED);
	}
	
	/**
	 * insert new nodes which parent is <code>parent</code> with indexes <code>indexes</code>
	 * by new nodes <code>newNodes</code>
	 * @param parent The parent of nodes are inserted
	 * @param indexFrom the lower index of the change range
	 * @param indexTo the upper index of the change range
	 * @param newNodes New nodes which are inserted
	 * @throws IndexOutOfBoundsException - indexFrom < 0 or indexTo > number of parent's children
	 */
	public void insert(Object parent, int indexFrom, int indexTo, Object[] newNodes) throws IndexOutOfBoundsException{
		ArrayList al = (ArrayList)parent;
		for(int i=indexFrom; i<=indexTo; i++){
			try{
				al.add(i, newNodes[i-indexFrom]);
			}catch(Exception exp){
				throw new IndexOutOfBoundsException("Out of bound: "+i+" while size="+al.size());
			}
		}
		fireEvent(parent,indexFrom,indexTo,TreeDataEvent.INTERVAL_ADDED);
		
	}
}

