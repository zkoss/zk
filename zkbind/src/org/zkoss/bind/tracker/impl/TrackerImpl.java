/* TrackerImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 24, 2011 7:31:14 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.tracker.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import org.zkoss.bind.impl.WeakIdentityMap;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.sys.LoadBinding;
import org.zkoss.bind.sys.PropertyBinding;
import org.zkoss.bind.sys.SaveBinding;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.bind.sys.tracker.TrackerNode;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.lang.Primitives;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Implementation of dependency tracking.
 * @author henrichen
 *
 */
public class TrackerImpl implements Tracker {
	private Map<Component, Map<Object, TrackerNode>> _compMap = new LinkedHashMap<Component, Map<Object, TrackerNode>>(); //comp -> path -> head TrackerNode
	private Map<Object, Set<TrackerNode>> _beanMap = new WeakIdentityMap<Object, Set<TrackerNode>>(); //bean -> Set of TrackerNode
	private Map<Object, Object> _equalBeanMap = new WeakHashMap<Object, Object>(); //bean -> bean (use to locate proxy bean)
	private Map<Object, Set<TrackerNode>> _nullMap = new HashMap<Object, Set<TrackerNode>>(); //property -> Set of head TrackerNode that eval to null
	
	public void addTracking(Component comp, String[] series, String[] srcpath, Binding binding) {
		//Track only LoadBinding
		if (!(binding instanceof LoadBinding)) {
			return;
		}
		
		final TrackerNodeImpl node = (TrackerNodeImpl) getOrCreateTrackerNode(comp, series);
		if (srcpath == null || srcpath.length == 0) {
			//node is leaf of this series, add the binding to it
			node.addBinding(binding);
		} else {
			//bug# 1: depends-on is not working in nested C->B->A when A changed
			final TrackerNode srcnode =  getOrCreateTrackerNode(comp, srcpath);
			node.addAssociate(srcnode); 
		}
	}

	private TrackerNode getOrCreateTrackerNode(Component comp, String[] series) {
		Map<Object, TrackerNode> nodes = _compMap.get(comp);
		if (nodes == null) {
			nodes = new HashMap<Object, TrackerNode>(4);
			_compMap.put(comp, nodes);
		}
		
		TrackerNode parentNode = null;
		for(String script : series) {
			TrackerNode node = null;
			if (parentNode == null) { //head node
				node = nodes.get(script);
				if (node == null) {
					node = new TrackerNodeImpl(script);
					nodes.put(script, node);
				}
			} else {
				node = parentNode.getDependent(script);
				if (node == null) {
					node = new TrackerNodeImpl(script);
				}
				parentNode.addDependent(script, node);
			}
			parentNode = node;
		}
		return parentNode;
	}
	
	public void removeTrackings(Component comp) {
		final Map<Object, TrackerNode> nodesMap = _compMap.remove(comp);
		if (nodesMap != null) {
			final Set<TrackerNode> removed = new HashSet<TrackerNode>();
			final Collection<TrackerNode> nodes = nodesMap.values();
			for (TrackerNode node : nodes) {
				removed.add(node);
				removed.addAll(node.getDependents());
			}
			removeNodes(_beanMap.values(), removed);
			removeNodes(_nullMap.values(), removed);
		}
	}

	private void getLoadBindingsPerProperty(Collection<TrackerNode> nodes, String prop, Set<LoadBinding> bindings, Set<Object> kidbases, Set<TrackerNode> visited) {
		if (".".equals(prop)) { //all base object
			for (TrackerNode node : nodes) {
				getLoadBindings0(node, bindings, kidbases, visited);
			}
		} else if ("*".equals(prop)) { //all binding properties of the base object
			for (TrackerNode node : nodes) {
				final Set<TrackerNode> kids = node.getDirectDependents();
				getNodesLoadBindings(kids, bindings, kidbases, visited);
			}
		} else {
			for (TrackerNode node : nodes) {
				final TrackerNode kid = node.getDependent(prop);
				if (kid != null) {
					getLoadBindings0(kid, bindings, kidbases, visited);
				}
			}
		}
	}
	
	public Set<LoadBinding> getLoadBindings(Object base, String prop) {
		final Set<LoadBinding> bindings = new HashSet<LoadBinding>();
		final Set<TrackerNode> visited = new HashSet<TrackerNode>();
		collectLoadBindings(base, prop, bindings, visited);
		return bindings;
	}
	
	private void collectLoadBindings(Object base, String prop, Set<LoadBinding> bindings, Set<TrackerNode> visited) {
		final Set<Object> kidbases = new HashSet<Object>(); //collect kid as base bean
		if (base != null) {
			if ("*".equals(base)) { //loadAll
				final Collection<Map<Object, TrackerNode>> nodesMaps = _compMap.values();
				if (nodesMaps != null) {
					for(Map<Object, TrackerNode> nodesMap : nodesMaps) {
						final Collection<TrackerNode> nodes = nodesMap.values();
						if (nodes != null) {
							getLoadBindingsPerProperty(nodes, prop, bindings, kidbases, visited);
						}
					}
				}
			} else {
				final Set<TrackerNode> nodes = getTrackerNodesByBean(base);
				if (nodes != null) {
					getLoadBindingsPerProperty(nodes, prop, bindings, kidbases, visited);
				}
			}
		} else { //base == null)
			if ("*".equals(prop)) {
				for (Set<TrackerNode> basenodes : _nullMap.values()) {
					getNodesLoadBindings(basenodes, bindings, kidbases, visited);
				}
			} else {
				final Set<TrackerNode> basenodes = _nullMap.get(prop);
				getNodesLoadBindings(basenodes, bindings, kidbases, visited);
			}
		}
		
		for (Object kidbase : kidbases) {
			collectLoadBindings(kidbase, "*", bindings, visited); //recursive, for kid base
		}
	}
	
	public void tieValue(Object comp, Object base, Object script, Object propName, Object value) {
		if (base == null) { //track from component
			//locate head TrackerNodes of this component
			final Map<Object, TrackerNode> bindingNodes = _compMap.get(comp);
			if (bindingNodes != null) {
				final TrackerNode node = bindingNodes.get(script);
				if (value != null) {
					addBeanMap(node, value);
				} else {
					removeAllBeanMap(node); //dependent nodes shall be null, too. Remove them from _beanMap 
					addNullMap(node); //head TrackerNode evaluate to null
				}
			}
		} else {
			final Set<TrackerNode> baseNodes = getTrackerNodesByBean(base);
			if (baseNodes != null) { //FormBinding will keep base nodes only (so no associated dependent nodes)
				for (TrackerNode baseNode : baseNodes) {
					final TrackerNode node = baseNode.getDependent(script);
					if (node == null) { //FormBinding will keep base nodes only (so no associated dependent nodes)
						continue;
					}
					if (BindELContext.isBracket((String)script)) {
						((TrackerNodeImpl)baseNode).tieProperty(propName, script);
					}
					if (value != null) {
						addBeanMap(node, value);
					} else {
						removeAllBeanMap(node); //dependent nodes shall be null, too. Remove them from _beanMap
					}
				}
			}
		}
	}
	//add node into the _beanMap
	private void addBeanMap(TrackerNode node, Object value) {
		//add node into _beanMap
		if (!value.equals(node.getBean())) {
			//try to remove from the _beanMap
			removeBeanMap(node);
			
			//add into _beanMap
			if (!isPrimitive(value)) {
				Set<TrackerNode> nodes = getTrackerNodesByBean(value);
				if (nodes == null) {
					nodes = new HashSet<TrackerNode>();
					_beanMap.put(value, nodes);
					_equalBeanMap.put(value, value); //prepare proxy bean map
				}
				nodes.add(node);
				//only when value is not a primitive that we shall store it
				node.setBean(value);
			}
		}
		
		//maybe a head node, try remove it from the nullMap
		removeNullMap(node);
	}
	
	//add head node into the _nullMap
	private void addNullMap(TrackerNode node) {
		//add node into _nullMap
		final Object propName = node.getFieldScript();
		Set<TrackerNode> nodes = _nullMap.get(propName);
		if (nodes == null) {
			nodes = new HashSet<TrackerNode>();
			_nullMap.put(propName, nodes);
		}
		nodes.add(node);
		
		//remove node from the _beanMap
		removeBeanMap(node);
	}
	
	//remove head node from the _nullMap
	private void removeNullMap(TrackerNode node) {
		final Object propName = node.getFieldScript();
		final Set<TrackerNode> nodes = _nullMap.get(propName);
		if (nodes != null) {
			nodes.remove(node);
			if (nodes.isEmpty()) {
				_nullMap.remove(propName);
			}
		}
	}
	
	//remove this node and all its dependent nodes from _beanMap
	private void removeAllBeanMap(TrackerNode node) {
		removeBeanMap(node);
		//all dependent node shall be removed, too.
		final Set<TrackerNode> kidnodes = node.getDependents();
		for(TrackerNode kid : kidnodes) {
			removeBeanMap(kid);
		}
	}
	
	//remove node from the _beanMap
	private void removeBeanMap(TrackerNode node) {
		final Object value = node.getBean();
		if (value != null) {
			node.setBean(null);
			final Set<TrackerNode> nodes = getTrackerNodesByBean(value);
			if (nodes != null) {
				nodes.remove(node); //remove this node from the _beanMap
				if (nodes.isEmpty()) {
					final Object proxy = _equalBeanMap.remove(value);
					if (proxy != null) {
						_beanMap.remove(proxy);
					} else {
						_beanMap.remove(value);
					}
				}
			}
		}
	}
	
	private boolean isPrimitive(Object value) {
		//null is deemed as primitive
		if (value == null) {
			return true;
		}
		final Class<? extends Object> cls = value.getClass();
		return cls.isPrimitive() //value is primitive 
			|| Primitives.toPrimitive(cls) != null //or a wrapper
			|| value instanceof String; //or a String
	}

	private void getNodesLoadBindings(Set<TrackerNode> basenodes, Set<LoadBinding> bindings, Set<Object> kidbases, Set<TrackerNode> visited) {
		if (basenodes != null) {
			for (TrackerNode node : basenodes) {
				if (node != null) {
					getLoadBindings0(node, bindings, kidbases, visited);
				}
			}
		}
	}
	
	private void getLoadBindings0(TrackerNode node, Set<LoadBinding> bindings, Set<Object> kidbases, Set<TrackerNode> visited) {
		if (visited.contains(node)) { //already visited
			return;
		}
		visited.add(node);
		
		final Set<Binding> nodebindings = node.getBindings();
		for(Binding binding : nodebindings) {
			if (binding instanceof LoadBinding) {
				bindings.add((LoadBinding)binding);
			}
		}
		
		//bug #1: depends-on is not working in nested C->B->A when A changed
		for(TrackerNode associate: node.getAssociates()) {
			getLoadBindings0(associate, bindings, kidbases, visited); //recursive
		}
		
		final Object kidbase = node.getBean();
		if (kidbase != null) {
			kidbases.add(kidbase);
		} else {
			//check all dependents
			Set<TrackerNode> nodes = node.getDependents(); //include all offspring and dependent nodes 
			for (TrackerNode kid : nodes) {
				final Set<Binding> kidbindings = kid.getBindings();
				for (Binding binding : kidbindings) {
					if (binding instanceof LoadBinding) {
						bindings.add((LoadBinding)binding);
					}
				}
			}
		}
	}
	
	//given base and postfix, found the associated TrackerNode. 
	@SuppressWarnings("unused")
	private Set<TrackerNode> getNodes(Object base, String postfix) {
		Set<TrackerNode> nodes = getTrackerNodesByBean(base);
		String[] props = postfix.split("\\.");
		for (String prop : props) {
			nodes = getDependents(nodes, prop);
		}
		return nodes;
	}
	
	//get dependents of a group of TrackerNodes.
	private Set<TrackerNode> getDependents(Set<TrackerNode> parentnodes, String prop) {
		final Set<TrackerNode> kidnodes = new HashSet<TrackerNode>();
		for (TrackerNode node : parentnodes) {
			final TrackerNode kid = node.getDependent(prop);
			if (kid != null) {
				kidnodes.add(kid);
			}
		}
		return kidnodes;
	}

	private void removeNodes(Collection<Set<TrackerNode>> nodesets, Collection<TrackerNode> removed) {
		for (final Iterator<Set<TrackerNode>> it = nodesets.iterator(); it.hasNext();) {
			final Set<TrackerNode> nodeset = it.next();
			nodeset.removeAll(removed);
			if (nodeset.isEmpty()) {
				it.remove();
			}
		}
	}
	
	private Set<TrackerNode> getTrackerNodesByBean(Object bean) {
		final Object proxy = _equalBeanMap.get(bean);
		return proxy != null ? _beanMap.get(proxy) : _beanMap.get(bean);
	}
	
	//------ debug dump ------//
	public void dump() {
		dumpCompMap();
		dumpBeanMap();
		dumpNullMap();
	}
	
	private void dumpBeanMap() {
		System.out.println("******* _beanMap: *********");
		for(Object bean : _beanMap.keySet()) {
			System.out.println("bean:"+bean+"------------");
			Set<TrackerNode> nodes = _beanMap.get(bean);
			for(TrackerNode node : nodes) {
				dumpNodeTree(node, 4);
			}
		}
	}
	
	private void dumpCompMap() {
		System.out.println("******* _compMap: *********");
		for(Component comp: _compMap.keySet()) {
			System.out.println("comp:"+comp+"------------");
			Map<Object, TrackerNode> nodes = _compMap.get(comp);
			for(Entry<Object, TrackerNode> entry : nodes.entrySet()) {
				System.out.println("----field:"+entry.getKey()+"");
				dumpNodeTree(entry.getValue(), 4);
			}
		}
	}

	private void dumpNullMap() {
		System.out.println("******* _nullMap: *********");
		for(Object field: _nullMap.keySet()) {
			System.out.println("field:"+field+"------");
			Set<TrackerNode> nodes = _beanMap.get(field);
			for(TrackerNode node : nodes) {
				dumpNodeTree(node, 4);
			}
		}
	}
	
	private void dumpNodeTree(TrackerNode node, int indent) {
		dumpNode(node, indent);
		for(TrackerNode kid : node.getDirectDependents()) {
			dumpNodeTree(kid, indent + 4);
		}
	}
	
	private void dumpNode(TrackerNode node, int spaces) {
		System.out.println(dumpSpace(spaces)+node.getFieldScript()+":"+node.getBean());
		dumpBindings(node, spaces);
		dumpPropNameMapping(node, spaces);
		dumpAssociate(node, spaces);
	}
	
	private void dumpNode0(TrackerNode node, int spaces) {
		System.out.println(dumpSpace(spaces)+node.getFieldScript()+":"+node.getBean());
		dumpBindings(node, spaces);
		dumpPropNameMapping(node, spaces);
	}
	
	private void dumpAssociate(TrackerNode node, int spaces) {
		if (node.getAssociates().isEmpty()) return; //don't dump if empty
		System.out.println(dumpSpace(spaces)+"[dependents:");
		for(TrackerNode dependent : node.getAssociates()) {
			dumpNode0(dependent, spaces+4); //avoid recursive
		}
		System.out.println(dumpSpace(spaces)+"]");
	}
	
	private void dumpBindings(TrackerNode node, int spaces) {
		if(node.getBindings().size()==0) return;//don't dump if empty
		System.out.println(dumpSpace(spaces)+"[bindings:");
		for(Binding binding : node.getBindings()) {
			dumpBinding(binding, spaces+4);
		}
		System.out.println(dumpSpace(spaces)+"]");
	}
	
	private void dumpBinding(Binding binding, int spaces) {
		if (binding instanceof PropertyBinding) {
			System.out.println(dumpSpace(spaces)+((PropertyBinding)binding).getPropertyString()+":"+binding);
		} else if (binding instanceof FormBinding) {
			System.out.println(dumpSpace(spaces)+((FormBinding)binding).getPropertyString()+":"+binding);
		}
	}
	
	private void dumpPropNameMapping(TrackerNode node, int spaces) {
		if(((TrackerNodeImpl)node).getPropNameMapping().size()==0) return;//don't dump if empty
		System.out.println(dumpSpace(spaces)+"[propertys:");
		for(Entry<Object, Object> entry : ((TrackerNodeImpl)node).getPropNameMapping().entrySet()) {
			dumpEntry(entry, spaces+4);
		}
		System.out.println(dumpSpace(spaces)+"]");
	}
	
	private void dumpEntry(Entry<Object, Object> entry, int spaces) {
		System.out.println(dumpSpace(spaces)+entry.getKey()+"="+entry.getValue());
	}
	
	private String dumpSpace(int space) {
		char[] spaces = new char[space];
		Arrays.fill(spaces, ' ');
		return new String(spaces);
	}
}
