/* AbstractTreeModel.java

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

import java.util.LinkedList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.io.Serializables;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * A skeletal implementation for {@link TreeModel}.
 *
 * <p>For introduction, please refer to
 * <a href="http://books.zkoss.org/wiki/ZK_Developer's_Reference/MVC/Model/Tree_Model">ZK Developer's Reference: Tree Model</a>.
 *
 * @author Jeff Liu
 * @since 3.0.0
 */
public abstract class AbstractTreeModel implements TreeModel, java.io.Serializable  {
	/**
	 * The root object to be return by method {@link #getRoot()}.
	 */
	private Object _root;
	
	private transient List _listeners = new LinkedList();
	
	/**
	 * Creates a {@link AbstractTreeModel}.
	 * @param root root of tree
	 */
	public AbstractTreeModel(Object root){
		_root = root;
	}
	/**
	* Return the root of tree
	* @return the root of tree
	*/
	public Object getRoot(){
		return _root;
	}
	
	/** Fires a {@link TreeDataEvent} for all registered listener
	 *
	 * <p>Note: you can invoke this method only in an event listener.
	 */
	public void fireEvent(Object node, int indexFrom, int indexTo, int evtType){
		final TreeDataEvent evt = new TreeDataEvent(this,evtType, node, indexFrom,indexTo);
		for (Iterator it = _listeners.iterator(); it.hasNext();)
			((TreeDataListener)it.next()).onChange(evt);
	}
	/**
	 * Returns the index of child in parent.
	 * If either parent or child is null, returns -1. If either parent or child don't belong to this tree model, returns -1. 
	 * <p>The default implementation iterates through all children of <code>parent</code>
	 * by invoking, and check if <code>child</code> is part of them.
	 * You could override it if you have a better algorithm.
	 * {@link #getChild}
	 * @param parent a node in the tree, obtained from this data source
     * @param child the node we are interested in 
	 * @return the index of the child in the parent, or -1 if either child or parent are null or don't belong to this tree model
	 * @since 5.0.6
	 */
	public int getIndexOfChild(Object parent, Object child) {
		final int cnt = getChildCount(parent);
		for (int j = 0; j < cnt; ++j)
			if (Objects.equals(child, getChild(parent, j)))
				return j;
		return -1;
	}

	//-- TreeModel --//
	public void addTreeDataListener(TreeDataListener l){
		_listeners.add(l);
	}
	
	//-- TreeModel --//
	public void removeTreeDataListener(TreeDataListener l){
		_listeners.remove(l);
	}
	
	//Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		Serializables.smartWrite(s, _listeners);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_listeners = new LinkedList();
		Serializables.smartRead(s, _listeners);
	}
}
