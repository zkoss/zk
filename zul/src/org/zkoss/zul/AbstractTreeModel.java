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

import org.zkoss.io.Serializables;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * A skeletal implementation for {@link TreeModel}.
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
