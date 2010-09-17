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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.io.Serializables;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * A skeletal implementation for {@link TreeModel}.
 *
 * @author Jeff Liu
 * @since ZK 3.0.0
 */
public abstract class AbstractTreeModel<E> implements TreeModel<E>, java.io.Serializable  {
	
	/**
	 * The root object to be return by method {@link #getRoot()}.
	 */
	private E _root;
	private transient List<TreeDataListener> _listeners = new LinkedList<TreeDataListener>();
	
	/**
	 * Constructor
	 * @param root - root of tree
	 */
	public AbstractTreeModel(E root){
		_root = root;
	}
	/**
	* Return the root of tree
	* @return the root of tree
	*/
	public E getRoot(){
		return _root;
	}
	
	/** Fires a {@link TreeDataEvent} for all registered listener
	 *
	 * <p>Note: you can invoke this method only in an event listener.
	 */
	protected void fireEvent(E node, int indexFrom, int indexTo, int evtType){
		final TreeDataEvent<E> evt = new TreeDataEvent<E>(this,evtType, node, indexFrom,indexTo);
		for (TreeDataListener l: _listeners)
			l.onChange(evt);
	}
	
	//-TreeModel-//
	public int[] getPath(E parent, E lastNode){
		List<Integer> l = new ArrayList<Integer>();
		dfSearch(l, parent, lastNode);
		int[] path = new int[l.size()];
		int i = 0;
		for (Integer v: l) {
			path[i++] = v.intValue();
		}
		return path;
	}
	
	/**
	 * Helper method:
	 * Depth first search to find the path which is from node to target
	 * @param al path
	 * @param node origin
	 * @param target destination
	 * @return whether the target is found or not
	 */
	private boolean dfSearch(List<Integer> path, E node, E target){
			if(node.equals(target)){
				return true;
			}
			else{
				int size = getChildCount(node);
				for(int i=0; i< size; i++){
					boolean flag = dfSearch(path,getChild(node,i),target);
					if(flag){
						path.add(0,new Integer(i));
						return true;
					}
				}
			}
			return false;
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

		_listeners = new LinkedList<TreeDataListener>();
		Serializables.smartRead(s, _listeners);
	}
}
