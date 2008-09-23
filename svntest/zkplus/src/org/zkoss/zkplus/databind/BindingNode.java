/* BindingNode.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Feb  1 16:16:54     2007, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.UiException;

/**
 * BindingNode that forms a databinding bean path dependant tree.
 *
 * @author Henri
 */
/*package*/ class BindingNode implements java.io.Serializable {
	private static final long serialVersionUID = 200808191424L;

	private LinkedHashSet _bindingSet = new LinkedHashSet(); //(Binding set of this expression BindingNode)
	private Map _kids = new LinkedHashMap(7); //(nodeid, BindingNode)
	private String _path; //path of this BindingNode
	private Set _sameNodes = new HashSet(); //BindingNode Set that refer to the same object
	private boolean _var; //a var node
	private String _nodeId; //node id of this BindingNode
	private boolean _root; //whether root node of a path
	private boolean _innerCollectionNode; //whether a collection in collection item node
	
	/** Constructor.
	 * @param path the path of this node in the expression dependant tree.
	 * @param var whether a _var variable binding node.
	 */
	public BindingNode(String path, boolean var, String id, boolean root) {
		_path = path;
		_sameNodes.add(this);
		_var = var;
		_nodeId = id;
		_root = root;
	}
	
	public LinkedHashSet getBindings() {
		return _bindingSet;
	}
	
	/** Get all Bindings below the given nodes (deepth first traverse).
	 */
	public LinkedHashSet getAllBindings() {
		Set walkedNodes = new HashSet(23);
		LinkedHashSet all = new LinkedHashSet(23*2);
		myGetAllBindings(all, walkedNodes);
		
		return all;
	}
	
	private void myGetAllBindings(LinkedHashSet all, Set walkedNodes) {
		if (walkedNodes.contains(this)) {
			return; //already walked, skip
		}
		
		//mark as walked already
		walkedNodes.add(this);
		
		for(final Iterator it = _kids.values().iterator(); it.hasNext();) {
			((BindingNode) it.next()).myGetAllBindings(all, walkedNodes); //recursive
		}
		
		for(final Iterator it = _sameNodes.iterator(); it.hasNext();) {
			final Object obj = it.next();
			if (obj instanceof BindingNode) {
				((BindingNode) obj).myGetAllBindings(all, walkedNodes); //recursive
			}
		}
		
		all.addAll(getBindings());
	}
		
	public String getPath() {
		return _path;
	}
	
	public String getNodeId() {
		return _nodeId;
	}

	public boolean isVar() {
		return _var;
	}
	
	public boolean isRoot() {
		return _root;
	}
	
	/** Add a binding in the BindingNode of the specified path.
	 * @param path the path of the specified BindingNode in the tree.
	 * @param binding the binding to be added to the specified BindingNode.
	 * @param varnameSet set with all _var names
	 */
	public void addBinding(String path, Binding binding, Set varnameSet) {
		final List nodeids = DataBinder.parseExpression(path, ".");
		if (nodeids.size() <= 0) {
			throw new UiException("Incorrect bean expression: "+path);
		}
		boolean var = varnameSet.contains(nodeids.get(0));
		BindingNode currentNode = this;
		for(final Iterator it = nodeids.iterator(); it.hasNext();) {
			final String nodeid = (String) it.next();
			if (nodeid == null) {
				throw new UiException("Incorrect bean expression: "+path);
			}
			BindingNode kidNode = (BindingNode) currentNode.getKidNode(nodeid);
			if (kidNode == null) { //if not found, then add one
				if ("/".equals(currentNode._path)) {
					kidNode = new BindingNode(nodeid, var, nodeid, true);
				} else {
					kidNode = new BindingNode(currentNode._path + "." + nodeid, var, nodeid, false);
				}
				currentNode.addKidNode(nodeid, kidNode);
			} else {
				var = var || kidNode._var;
			}
			currentNode = kidNode;
		}
		if (currentNode == this) {
			throw new UiException("Incorrect bean expression: "+path);
		}
		currentNode.addBinding(binding);
		if ("_var".equals(binding.getAttr())) {
			currentNode._innerCollectionNode =  DataBinder.hasTemplateOwner(binding.getComponent());
		}
	}
	
	public boolean isInnerCollectionNode() {
		return _innerCollectionNode;
	}
	
	/** Add a binding to this BindingNode.
	 * @param binding the binding to be added to this node.
	 */
	public void addBinding(Binding binding) {
		_bindingSet.add(binding);
	}
	
	/** Locate the BindingNode of the specified path.
	 * @param path the path of the specified BindingNode in the tree.
	 */
	public BindingNode locate(String path) {
		BindingNode currentNode = this;
		final List nodeids = DataBinder.parseExpression(path, ".");
		for(final Iterator it = nodeids.iterator(); it.hasNext(); ) {
			final String nodeid = (String) it.next();
			if (nodeid == null) {
				throw new UiException("Incorrect format of bean expression: "+path);
			}
			currentNode = (BindingNode) currentNode.getKidNode(nodeid);
			if (currentNode == null) {
				return null; 
			}
		}
		return currentNode == this ? null : currentNode;
	}
	
	/** Get root node of this node.
	 */
	public BindingNode getRootNode(BindingNode superNode) {
		if (isRoot()) {
			return this;
		}
		final int j = getPath().indexOf(".");
		final String path = (j < 0) ? getPath() : getPath().substring(0, j);
		return superNode.locate(path);
	}
	
	/** get the sameNodes of this BindingNode.
	 */
	public Set getSameNodes() {
		return _sameNodes;
	}
	
	/** merge and set the given other sameNodes as sameNodes of this BindingNode.
	 */
	public void mergeAndSetSameNodes(Set other) {
		if (other == _sameNodes) { //same set, no need to merge
			return;
		}
		if (_sameNodes != null) {
			other.addAll(_sameNodes);
		}
		_sameNodes = other;
	}
	
	public String toString() {
		return _path;
	}

	// Get kid nodes
	/*package*/ Collection getKidNodes() {
		return _kids.values();
	}
	
	// Get kid nodes of the specified nodeid.
	/*package*/ BindingNode getKidNode(String nodeid) {
		return (BindingNode) _kids.get(nodeid);
	}

	private void addKidNode(String nodeid, BindingNode kid) {
		_kids.put(nodeid, kid);
	}
	
}
