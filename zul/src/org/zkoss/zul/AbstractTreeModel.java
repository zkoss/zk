/* AbstractTreeModel.java

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * A skeletal implementation for {@link TreeModel}.
 *
 * @author Jeff Liu
 */
public abstract class AbstractTreeModel implements TreeModel, java.io.Serializable  {
	
	private Object _root;
	
	private transient List _listeners = new LinkedList();
	
	/**
	* Set the root of tree
	* @param root the root of tree
	*/
	public void setRoot(Object root){
		_root = root;
	}
	
	/**
	* Return the root of tree
	* @return the root of tree
	*/
	public Object getRoot(){
		return _root;
	}
	
	/**
	* See {@link TreeModel}
	*/
	public Object getChild(Treeitem ti, Tree t){
		return getNodeByPath(getTreePath(t,ti),getRoot());
	}
	
	/** Fires a {@link TreeDataEvent} for all registered listener
	 *
	 * <p>Note: you can invoke this method only in an event listener.
	 */
	protected void fireEvent(Object node, int index, int evtType){
		final TreeDataEvent evt = new TreeDataEvent(this,evtType, node, index);
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
	
	/**
	 * return the path which is from ZK Component root to ZK Component lastNode 
	 * @param root
	 * @param lastNode
	 * @return
	 */
	private ArrayList getTreePath(Component root, Component lastNode){
		ArrayList al = new ArrayList();
		Component curNode = lastNode;
		while(!root.equals(curNode)){
			if(curNode instanceof Treeitem){
				al.add(((Treeitem)curNode).indexOf());
			}
			curNode = curNode.getParent();
		}
		return al;
	}
	
	/**
	 * Return the path which is from tree root to a given node
	 */
	public ArrayList getPath(Object node){
		return getTreePath(getRoot(),node);
	}
	
	/**
	 * return the tree path which is from root to lastNode
	 * @param root
	 * @param lastNode
	 * @return
	 */
	public ArrayList getTreePath(Object root, Object lastNode){
		ArrayList al = new ArrayList();
		dfSearch(al, root, lastNode);
		return al;
	}
	
	/**
	 * Depth first search to find the path which is from node to target
	 * @param al path
	 * @param node origin
	 * @param target destination
	 * @return whether the target is found or not
	 */
	private boolean dfSearch(ArrayList al, Object node, Object target){
			if(node.equals(target)){
				return true;
			}
			else{
				int size = getChildCount(node);
				for(int i=0; i< size; i++){
					boolean flag = dfSearch(al,getChild(node,i),target);
					if(flag){
						al.add(0,i);
						return true;
					}
				}
			}
			return false;
	}
	
	/**
	 * Get the node from tree by given path
	 * @param path
	 * @param root
	 * @return
	 */
	private Object getNodeByPath(ArrayList path, Object root)
	{
		Object node = root;
		for(int i=path.size()-2; i >= 0; i--){
			node = getChild(node, Integer.parseInt(path.get(i).toString()));
		}
		return node;
	}

}
