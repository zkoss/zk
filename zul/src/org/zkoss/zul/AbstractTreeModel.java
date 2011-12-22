/* AbstractTreeModel.java

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.io.Serializables;

import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.ext.Openable;
import org.zkoss.zul.ext.Selectable;

/**
 * A skeletal implementation for {@link TreeModel}.
 *
 * <p>For introduction, please refer to
 * <a href="http://books.zkoss.org/wiki/ZK_Developer's_Reference/MVC/Model/Tree_Model">ZK Developer's Reference: Tree Model</a>.
 *
 * @author Jeff Liu
 * @since 3.0.0
 */
public abstract class AbstractTreeModel<E>
implements TreeModel<E>, Selectable<E>, Openable<E>, java.io.Serializable  {
	/**
	 * The root object to be return by method {@link #getRoot()}.
	 */
	private E _root;
	private transient List<TreeDataListener> _listeners = new LinkedList<TreeDataListener>();
	private Set<E> _selection = new HashSet<E>();
	private Set<E> _openSet = new HashSet<E>();
	
	/**
	 * Creates a {@link AbstractTreeModel}.
	 * @param root root of tree
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
	public void fireEvent(E node, int indexFrom, int indexTo, int evtType){
		final TreeDataEvent<E> evt = new TreeDataEvent<E>(this,evtType, node, indexFrom,indexTo);
		for (TreeDataListener l: _listeners)
			l.onChange(evt);
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
	public int getIndexOfChild(E parent, E child) {
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
	
	//Selectable
	public Set<E> getSelection() {
		return Collections.unmodifiableSet(_selection);
	}
	
	public void addSelection(E obj) {
		_selection.add(obj);
	}
	
	public void removeSelection(Object obj) {
		_selection.remove(obj);
	}
	
	public void clearSelection() {
		_selection.clear();
	}
	
	protected void removeAllSelection(Collection<? extends E> c) {
		_selection.removeAll(c);
	}
	
	protected void retainAllSelection(Collection<? extends E> c) {
		_selection.retainAll(c);
	}
	
	//Openable
	public void setOpen(E obj, boolean open) {
		if (open)
			_openSet.add(obj);
		else
			_openSet.remove(obj);
	}
	
	public boolean isOpen(E obj) {
		return _openSet.contains(obj);
	}
	
	public void clearOpen() {
		_openSet.clear();
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

	//-TreeModel-//
	/**
	 * @deprecated As of release 5.0.6, it was replaced by {@link #getIndexOfChild}.
	 * This method was implemented to provide backward compatibility.
	 */
	@SuppressWarnings("unchecked")
	public int[] getPath(Object parent, Object lastNode){
		return Tree.getPath((TreeModel)this, parent, lastNode);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		final AbstractTreeModel clone;
		try {
			clone = (AbstractTreeModel) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		clone._listeners = new LinkedList<TreeDataListener>();
		clone._selection = new HashSet<E>(_selection);
		clone._openSet = new HashSet<E>(_openSet);
		
		return clone;
	}
}
