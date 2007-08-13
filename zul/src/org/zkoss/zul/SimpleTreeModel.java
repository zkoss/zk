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
	public SimpleTreeModel(Object root){
		super.setRoot(root);
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
	 * @param indexes The indexes of nodes are modified
	 * @param values The new values of nodes are modified
	 */
	public void set(Object parent, int[] indexes, Object[] values)
	{
		ArrayList al = (ArrayList)parent;
		for(int i=0; i<indexes.length;i++)
			al.set(indexes[i], values[i]);
		fireEvent(parent,indexes,TreeDataEvent.CONTENTS_CHANGED);
	}
	
	/**
	 * remove the nodes which parent is <code>parent</code> with indexes <code>indexes</code>
	 * @param parent The parent of nodes are removed
	 * @param index The indexes of nodes are removed
	 */
	public void remove(Object parent, int[] indexes){
		ArrayList al = (ArrayList)parent;	
		
		for(int i=0; i<indexes.length;i++)
		try{	
			al.remove(indexes[i]);
		}
		catch(Exception exp){
			throw new IndexOutOfBoundsException("Out of bound: "+indexes[i]+" while size="+al.size());
		}
		fireEvent(parent,indexes,TreeDataEvent.NODE_REMOVED);
		
		
	}
	
	/**
	 * append new nodes which parent is <code>parent</code>
	 * by new nodes <code>newNodes</code>
	 * @param parent The parent of nodes are appended
	 * @param newNodes New nodes which are appended
	 */
	public void add(Object parent, Object[] newNodes){
		ArrayList al = (ArrayList)parent;
		int[] indexes = new int[newNodes.length];
		for(int i=0; i<newNodes.length;i++)
		{
			indexes[i] = al.size()-1 +i;
			al.add(newNodes[i]);
		}		
		fireEvent(parent,indexes,TreeDataEvent.NODE_ADDED);
	}
	
	/**
	 * insert new nodes which parent is <code>parent</code> with indexes <code>indexes</code>
	 * by new nodes <code>newNodes</code>
	 * @param parent The parent of nodes are inserted
	 * @param index The index of nodes are inserted
	 * @param newNode New nodes which are inserted
	 */
	public void insert(Object parent, int[] indexes, Object[] newNodes){
		ArrayList al = (ArrayList)parent;
		for(int i=0; i<indexes.length; i++)
			al.add(indexes[i], newNodes[i]);
		fireEvent(parent,indexes,TreeDataEvent.NODE_ADDED);
	}
}

