/* TrackerImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 24, 2011 7:31:14 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.tracker.impl;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import org.zkoss.bind.impl.AllocUtil;
import org.zkoss.bind.impl.IndirectBinding;
import org.zkoss.bind.impl.WeakIdentityMap;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.ChildrenBinding;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.sys.LoadBinding;
import org.zkoss.bind.sys.LoadChildrenBinding;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.bind.sys.PropertyBinding;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.bind.sys.tracker.TrackerNode;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.util.IdentityHashSet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tabbox;

/**
 * Implementation of dependency tracking.
 * @author henrichen
 * @since 6.0.0
 */
public class TrackerImpl implements Tracker, Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	protected Map<Component, Map<Object, TrackerNode>> _compMap; //comp -> path -> head TrackerNode
	protected Map<Object, Set<TrackerNode>> _nullMap = new LinkedHashMap<Object, Set<TrackerNode>>(); //property -> Set of head TrackerNode that eval to null
	protected transient Map<Object, Set<TrackerNode>> _beanMap = new WeakIdentityMap<Object, Set<TrackerNode>>(); //bean -> Set of TrackerNode
	protected transient EqualBeansMap _equalBeansMap; //bean -> beans (use to manage equal beans)
	
	public TrackerImpl() {
		_equalBeansMap = newEqualBeansMap(); 
		_compMap = initCompMap();
	}
	
	// provide a way for sub-class to override it for some situation
	protected Map<Component, Map<Object, TrackerNode>> initCompMap() {
		return new LinkedHashMap<Component, Map<Object, TrackerNode>>();
	}

	protected EqualBeansMap newEqualBeansMap() {
		return new EqualBeansMap();
	}
	
	public void addTracking(Component comp, String[] series, Binding binding) {
		//Track only LoadBinding
		if (!(binding instanceof LoadBinding)) {
			return;
		}
		
		final TrackerNode node = getOrCreateTrackerNode(comp, series);
		
		//node is leaf of this series, add the binding to it
		node.addBinding(binding);
	}
	
	public void addDependsOn(Component srcComp, String[] srcSeries, Binding srcBinding, Component dependsOnComp, String[] dependsOnSeries) {
		//Track only LoadBinding
		if (!(srcBinding instanceof LoadBinding)) {
			return;
		}
		if (dependsOnComp == null) {
			dependsOnComp = srcComp; //share same component context for @DependsOn case
		}
		final TrackerNode dependsOnNode = getOrCreateTrackerNode(dependsOnComp, dependsOnSeries);
		//bug# 1: depends-on is not working in nested C->B->A when A changed
		final TrackerNode srcnode =  getOrCreateTrackerNode(srcComp, srcSeries);
		dependsOnNode.addAssociate(srcnode); 
	}
	
	protected TrackerNode getOrCreateTrackerNode(Component comp, String[] series) {
		Map<Object, TrackerNode> nodes = _compMap.get(comp);
		
		TrackerNode parentNode = null;
		for(String script : series) {
			TrackerNode node = null;
			if (parentNode == null) { //head node
				node = nodes == null ? null : nodes.get(script);
				if (node == null) {
					node = newTrackerNode(script);
					//ZK-2289
					Map<Object, TrackerNode> nodes0 = AllocUtil.inst.putMap(nodes, script, node);
					if (nodes != nodes0) { //Yes, use != instead of !equals()
						_compMap.put(comp,  nodes0);
					}
				}
			} else {
				node = parentNode.getDependent(script);
				if (node == null) {
					node = newTrackerNode(script);
				}
				parentNode.addDependent(script, node);
			}
			parentNode = node;
		}
		return parentNode;
	}
	
	//ZK-1989, sub-class could override this method to provide better tracker-node impl.
	protected TrackerNode newTrackerNode(Object script){
		return new TrackerNodeImpl(AllocUtil.inst.processScript(script)); //ZK-2289
	}
	
	public void removeTrackings(Set<Component> comps) {
		final Set<TrackerNode> removed = new LinkedHashSet<TrackerNode>();
		for(Component comp:comps){
			final Map<Object, TrackerNode> nodesMap = _compMap.remove(comp);
			if (nodesMap != null) {
				final Collection<TrackerNode> nodes = nodesMap.values();
				for (TrackerNode node : nodes) {
					removed.add(node);
					removed.addAll(node.getDependents());
				}
			}
		}
		if(removed.size()>0){
			removeAllFromBeanMap(removed);
			removeAllFromNullMap(removed);
		}
	}
	public void removeTrackings(Component comp) {
		final Map<Object, TrackerNode> nodesMap = _compMap.remove(comp);
		if (nodesMap != null) {
			final Set<TrackerNode> removed = new LinkedHashSet<TrackerNode>();
			final Collection<TrackerNode> nodes = nodesMap.values();
			for (TrackerNode node : nodes) {
				removed.add(node);
				removed.addAll(node.getDependents());
			}
			removeAllFromBeanMap(removed);
			removeAllFromNullMap(removed);
		}
	}

	//remove all specified nodes from the _nullMap 
	protected void removeAllFromNullMap(Set<TrackerNode> removed) {
		removeNodes(_nullMap.values(), removed);
	}
	
	private void getLoadBindingsPerProperty(Collection<TrackerNode> nodes, String prop, LinkedHashSet<LoadBinding> bindings, LinkedHashSet<Object> kidbases, Set<TrackerNode> visited) {
		if (".".equals(prop)) { //all base object
			for (TrackerNode node : nodes) {
				getLoadBindings0(node, bindings, kidbases, visited);
			}
		} else if ("*".equals(prop)) { //all binding properties of the base object
			for (TrackerNode node : nodes) {
				final Set<TrackerNode> kids = node.getDirectDependents();
				
				// refix for ZK-1787 test cases.
				for (LoadBinding binding : node.getLoadBindings()) {
					if (testBindingRendererCase(binding))
						bindings.add(binding);
				}
				
				getNodesLoadBindings(kids, bindings, kidbases, visited);
			}
		} else {
			boolean bracket = BindELContext.isBracket(prop);
			int index = bracket ? Integer.parseInt(prop.substring(1, prop.length() - 1)) : -1; 
			for (TrackerNode node : nodes) {
				Set<TrackerNode> dependents = node.getDependents(prop);
				if (bracket && dependents.isEmpty()) {
					Object bean = node.getBean();
					if (bean instanceof List<?>) {
						kidbases.add(((List<?>) bean).get(index));
					} else if (bean instanceof ListModel<?>) {
						if (bean instanceof ListModelArray){
		            		kidbases.add(((ListModelArray<Object>)bean).get(index));
		            	} else if(bean instanceof ListModelList<?>){
		            		kidbases.add(((ListModelList<Object>)bean).get(index));
		            	}
					}
				} else {
					for (TrackerNode kid : dependents) {
						if (kid != null) {
							getLoadBindings0(kid, bindings, kidbases, visited);
						}
					}
				}
			}
		}
	}
	
	// refix for ZK-2552
	// we need to add an indirect binding here.
	private boolean testBindingRendererCase(LoadBinding binding) {
		if (binding instanceof LoadChildrenBinding)
			return true;
		if (binding instanceof LoadPropertyBinding) {
			LoadPropertyBinding lb = (LoadPropertyBinding) binding;
			if ("model".equals(lb.getFieldName())) {
				Component comp = lb.getComponent();
				if (comp instanceof Listbox
						|| comp instanceof Grid
						|| comp instanceof Tabbox
						|| comp instanceof Radiogroup
						|| comp instanceof Combobox)
					return true;
			}
		}
		
		return false;
	}
	
	public Set<LoadBinding> getLoadBindings(Object base, String prop) {
		final LinkedHashSet<LoadBinding> bindings = new LinkedHashSet<LoadBinding>();
		final Set<TrackerNode> visited = new LinkedHashSet<TrackerNode>();
		collectLoadBindings(base, prop, bindings, visited);
		return bindings;
	}
	
	protected Collection<TrackerNode> getAllTrackerNodes() {
		Set<TrackerNode> all = null;
		final Collection<Map<Object, TrackerNode>> nodesMaps = _compMap.values();
		if (nodesMaps != null && !nodesMaps.isEmpty()) {
			all = new LinkedHashSet<TrackerNode>();
			for(Map<Object, TrackerNode> nodesMap : nodesMaps) {
				final Collection<TrackerNode> nodes = nodesMap.values();
				if (nodes != null) {
					all.addAll(nodes);
				}
			}
		}
		return all;
	}
	
	private void collectLoadBindings(Object base, String prop, LinkedHashSet<LoadBinding> bindings, Set<TrackerNode> visited) {
		final LinkedHashSet<Object> kidbases = new LinkedHashSet<Object>(); //collect kid as base bean
		if (base != null) {
			if ("*".equals(base)) { //loadAll, when base == "*"
				final Collection<TrackerNode> nodes = getAllTrackerNodes();
				if (nodes != null) {
					getLoadBindingsPerProperty(nodes, prop, bindings, kidbases, visited);
				}
			} else {
				final Set<TrackerNode> nodes = getAllTrackerNodesByBean(base);
				if (nodes != null && !nodes.isEmpty()) {
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
	
	protected TrackerNode getTrackerNodePerComponentScript(Object comp, Object script) {
		//locate head TrackerNodes of this component
		final Map<Object, TrackerNode> bindingNodes = _compMap.get(comp);
		return bindingNodes != null ? bindingNodes.get(script) : null;
	}

	public void tieValue(Object comp, Object base, Object script, Object propName, Object value, Object basePath) {
		if (value instanceof IndirectBinding) {
			value = ((IndirectBinding) value).getValue(null);
		}
		if (base == null) { //track from component
			final TrackerNode node = getTrackerNodePerComponentScript(comp, script);
			//ZK-877: NPE in a save only binding
			//No corresponding LoadBinding with the head script in the specified component. 
			if (node != null) {
				if (value != null) {
					addBeanMap(node, value, basePath);
				} else {
					removeAllBeanMap(node); //dependent nodes shall be null, too. Remove them from _beanMap 
					addNullMap(node); //head TrackerNode evaluate to null
				}
			}
		} else {
			
			final Set<TrackerNode> baseNodes = getAllTrackerNodesByBean(base);
			if (baseNodes != null) { //FormBinding will keep base nodes only (so no associated dependent nodes)
				final Set<TrackerNode> propNodes = new LinkedHashSet<TrackerNode>(); //normal nodes; i.e. a base + property node. e.g. vm.selectedPerson
				for (TrackerNode baseNode : baseNodes) {
					for (TrackerNode node : baseNode.getDependents(script)) {
						if (node == null) { //FormBinding will keep base nodes only (so no associated dependent nodes)
							continue;
						}

						// ZK-2552: we need to ignore if the target component in reference binding
						// is different from the given comopnent, because they may bind with a same
						// data object with the same EL expression.
						boolean ignore = false;
						for (ReferenceBinding refBinding : node.getReferenceBindings()) {
							if (refBinding.getComponent() == comp) {
								ignore = false;
								break;
							}
							ignore = true;
						}
						// don't need to update with a wrong target component.
						if (ignore)
							continue;
						
						propNodes.add(node);
						if (BindELContext.isBracket((String)script)) {
							baseNode.tieProperty(propName, script);
						}
					}
				}

				if (value != null) {
					for (TrackerNode node : propNodes) { //normal nodes
						addBeanMap(node, value, basePath);
					}
				} else { //value == null
					for (TrackerNode node : propNodes) { //normal nodes
						removeAllBeanMap(node); //dependent nodes shall be null, too. Remove them from _beanMap
					}
				}
			}
		}
	}
	
	//add node into the _beanMap
	protected void addBeanMap(TrackerNode node, Object value, Object basePath) {
		//add node into _beanMap
		if (!testEqualsBean(node.getBean(), value)) {
			//try to remove from the _beanMap
			removeBeanMap(node);
			
			//add into _beanMap

			// comment out the if condition is to support the method expression with parameter for tracker nodes
			// For example, MODEL$uuid[self.getAttribute("something").getCurrentIndex(self)]
			// If the returned value of self.getAttribute("something") is an immutable object,
			// we still need it to be trackable so that the tracker node with getCurrentIndex(self) will be found
			// when the given seeking object is that retuned by getAttribute("something")
			//if (!BindELContext.isImmutable(value)) {
				Set<TrackerNode> nodes = _beanMap.get(value);
				//ZK-2289
				final Set<TrackerNode> nodes0 = AllocUtil.inst.addLinkedHashSet(nodes, node);
				if (nodes == null) {
					_equalBeansMap.put(value);
				}
				if (nodes != nodes0) { // yes, !=; not !equals
					_beanMap.put(value, nodes0);
				}
				//only when value is not a primitive that we shall store it
				node.setBean(value);
			//}
		}
		
		//maybe a head node, try remove it from the nullMap
		removeNullMap(node);
	}
	
	//add head node into the _nullMap
	private void addNullMap(TrackerNode node) {
		//add node into _nullMap
		final Object propName = node.getFieldScript();
		final Set<TrackerNode> nodes = _nullMap.get(propName);
		//ZK-2289
		final Set<TrackerNode> nodes0 = AllocUtil.inst.addLinkedHashSet(nodes, node);
		if (nodes != nodes0) { //Yes, user != instead of !equals()
			_nullMap.put(propName, nodes0);
		}
		
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
	protected void removeBeanMap(TrackerNode node) {
		final Object value = node.getBean();
		if (value != null) {
			node.setBean(null);
			final Set<TrackerNode> nodes = _beanMap.get(value);
			if (nodes != null) {
				nodes.remove(node); //remove this node from the _beanMap
				if (nodes.isEmpty()) {
					_equalBeansMap.remove(value); //sync the equalBeanMap 
					_beanMap.remove(value);
				}
			}
		}
	}
	
	private void getNodesLoadBindings(Set<TrackerNode> basenodes, LinkedHashSet<LoadBinding> bindings, LinkedHashSet<Object> kidbases, Set<TrackerNode> visited) {
		if (basenodes != null) {
			for (TrackerNode node : basenodes) {
				if (node != null) {
					getLoadBindings0(node, bindings, kidbases, visited);
				}
			}
		}
	}
	
	private void getLoadBindings0(TrackerNode node, LinkedHashSet<LoadBinding> bindings, Set<Object> kidbases, Set<TrackerNode> visited) {
		if (visited.contains(node)) { //already visited
			return;
		}
		visited.add(node);
		
		bindings.addAll(node.getLoadBindings());
		final Set<ReferenceBinding> refBindings = node.getReferenceBindings();
		bindings.addAll(refBindings);
		for (ReferenceBinding refBinding : refBindings) {
			refBinding.invalidateCache();
			//ZK-950: The expression reference doesn't update while change the instant of the reference
			//Have to load bindings that refer this ReferenceBinding as well
			collectLoadBindings(refBinding, ".", bindings, visited); //recursive
		}
		
		//bug #1: depends-on is not working in nested C->B->A when A changed
		for(TrackerNode associate: node.getAssociates()) {
			getLoadBindings0(associate, bindings, kidbases, visited); //recursive
		}
		
		final Object kidbase = node.getBean();
		if (kidbases != null && kidbase != null) {
			kidbases.add(kidbase);
		} else {
			//check dependents
			final Set<TrackerNode> nodes = node.getDirectDependents(); 
			for (TrackerNode kid : nodes) {
				getLoadBindings0(kid, bindings, null, visited); //recursive
			}
		}
	}
	
	//given base and postfix, found the associated TrackerNode. 
	@SuppressWarnings("unused")
	private Set<TrackerNode> getNodes(Object base, String postfix) {
		Set<TrackerNode> nodes = getAllTrackerNodesByBean(base);
		String[] props = postfix.split("\\.");
		for (String prop : props) {
			nodes = getDependents(nodes, prop);
		}
		return nodes;
	}
	
	//get dependents of a group of TrackerNodes.
	private Set<TrackerNode> getDependents(Set<TrackerNode> parentnodes, String prop) {
		final Set<TrackerNode> kidnodes = new LinkedHashSet<TrackerNode>();
		for (TrackerNode node : parentnodes) {
			for (TrackerNode kid : node.getDependents(prop)) {
				if (kid != null) {
					kidnodes.add(kid);
				}
			}
		}
		return kidnodes;
	}

	//remove all specified nodes from the _beanMap 
	protected void removeAllFromBeanMap(Collection<TrackerNode> removed) {
		final Collection<Entry<Object, Set<TrackerNode>>> nodesets = _beanMap.entrySet(); 
		for (final Iterator<Entry<Object, Set<TrackerNode>>> it = nodesets.iterator(); it.hasNext();) {
			final Entry<Object, Set<TrackerNode>> nodeset = it.next();
			final Object bean = nodeset.getKey();
			nodeset.getValue().removeAll(removed);
			if (nodeset.getValue().isEmpty()) {
				it.remove();
				_equalBeansMap.remove(bean);
			}
		}
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
	
	private Set<TrackerNode> getAllTrackerNodesByBean(Object bean) {
		final Set<TrackerNode> results = new LinkedHashSet<TrackerNode>();
		getAllTrackerNodesByBean0(bean, results);
		return results;
	}
	
	private void getAllTrackerNodesByBean0(Object bean, Set<TrackerNode> results) {
		final Set<Object> beans = _equalBeansMap.getEqualBeans(bean); //return a set of equal beans
		final Set<TrackerNode> nodes = new LinkedHashSet<TrackerNode>();
		for (Object obj : beans) {
			Set<TrackerNode> beanNodes = _beanMap.get(obj);
			if(beanNodes!=null){//zk-1185, _beanMap could contains no such entry, and returned null.
				nodes.addAll(beanNodes);
			}
		}
		results.addAll(nodes);
		getAllTrackerNodesByBeanNodes(nodes, results);
	}
	
	//ZK-950: The expression reference doesn't update while change the instant of the reference
	//Check if the passed in bean nodes contains ReferenceBindings; have to collect those
	//nodes that refers those ReferenceBindings as well
	private void getAllTrackerNodesByBeanNodes(Set<TrackerNode> nodes, Set<TrackerNode> results) {
		for (TrackerNode node : nodes) {
			final Set<ReferenceBinding> refBindings = node.getReferenceBindings();
			for (ReferenceBinding refBinding : refBindings) {
				getAllTrackerNodesByBean0(refBinding, results); //recursive
			}
		}
	}
	
	//Returns equal beans with the given bean in an IdentityHashSet() 
	public Set<Object> getEqualBeans(Object bean) {
		return _equalBeansMap.getEqualBeans(bean); //return a set of equal beans
	}
	
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		
		_beanMap = new WeakIdentityMap<Object, Set<TrackerNode>>(); //bean -> Set of TrackerNode
		_equalBeansMap = new EqualBeansMap(); //bean -> beans (use to manage equal beans)
	}
	protected static boolean testEqualsBean(Object nodeBean, Object bean) {
		if (nodeBean == bean)
			return true;
		if (nodeBean instanceof WeakReference) {
			nodeBean = ((WeakReference) nodeBean).get();
		}
		return bean.equals(nodeBean);
	}
	protected static class EqualBeansMap {
		private transient WeakHashMap<Object, EqualBeans> _innerMap = new WeakHashMap<Object, EqualBeans>(); //bean -> EqualBeans
		private transient WeakIdentityMap<Object, EqualBeans> _identityMap = new WeakIdentityMap<Object, EqualBeans>(); //bean -> EqualBeans
		
		//bug #ZK-678: NotifyChange on Map is not work
		private void syncInnerMap(EqualBeans equalBeans, Object bean) {
			//hashCode of bean has changed, must reset
			boolean found = false;
			final WeakHashMap<Object, EqualBeans> newMap = new WeakHashMap<Object, EqualBeans>(_innerMap.size());
			//ZK-781. Copy one by one to reset _innerMap
			for(Iterator<Entry<Object, EqualBeans>> it = _innerMap.entrySet().iterator(); it.hasNext();) {
				final Entry<Object, EqualBeans> entry = it.next();
				final EqualBeans beans = entry.getValue();
				if (equalBeans.equals(beans)) { //found
					found = true;
					continue;
				}
				newMap.put(entry.getKey(), entry.getValue());
			}
			if (found) {
				_innerMap = newMap;
				//reput equalBeans (item inside might not equal to each other any more)
				for (Object b : equalBeans.getBeans()) {
					_identityMap.remove(b);
					put(b); //recursive
				}
			}
		}
		
		public void put(Object bean) {
			EqualBeans equalBeans = _innerMap.get(bean);
			if (equalBeans == null) { //hashcode might changed
				equalBeans = _identityMap.remove(bean);
				if (equalBeans != null) { //hashcode is changed
					syncInnerMap(equalBeans, bean);
					return;
				} else { //a new bean
					equalBeans = new EqualBeans(bean);
					_innerMap.put(bean, equalBeans);
				}
			} else {
				equalBeans.put(bean);
			}
			_identityMap.put(bean, equalBeans);
		}
		
		public void remove(Object bean) {
			EqualBeans equalBeans = _innerMap.remove(bean);
			if (equalBeans != null) {
				_identityMap.remove(bean);
				removeFromEqualBeansAndReput(equalBeans, bean);
			} else { //hashcode might changed
				equalBeans = _identityMap.remove(bean);
				if (equalBeans != null) { //hashcode is changed
					//hashCode of bean has changed, must reset
					boolean found = false;
					final WeakHashMap<Object, EqualBeans> newMap = new WeakHashMap<Object, EqualBeans>(_innerMap.size());   
					//ZK-781. Copy one by one to reset _innerMap
					for(Iterator<Entry<Object, EqualBeans>> it = _innerMap.entrySet().iterator(); it.hasNext();) {
						final Entry<Object, EqualBeans> entry = it.next();
						final EqualBeans beans = entry.getValue();
						if (equalBeans.equals(beans)) { //found
							found = true;
							continue;
						}
						newMap.put(entry.getKey(), entry.getValue());
					}
					if (found) {
						_innerMap = newMap;
						removeFromEqualBeansAndReput(equalBeans, bean); //remove from EqualBeans
					}
				}
			}
		}
		
		private void removeFromEqualBeansAndReput(EqualBeans equalBeans, Object bean) {
			final Object proxy = equalBeans.remove(bean);
			if (!equalBeans.isEmpty()) {
				_innerMap.put(proxy, equalBeans); //reput into _innerMap with new Proxy
			}
		}
		
		public Set<Object> getEqualBeans(Object bean) {
			EqualBeans equalBeans = _innerMap.get(bean);
			if (equalBeans == null) { //hashcode might changed
				equalBeans = _identityMap.remove(bean);
				if (equalBeans != null) { //hashcode is changed
					syncInnerMap(equalBeans, bean);
					equalBeans = _identityMap.get(bean);
				}
			}
			return equalBeans == null ? Collections.emptySet() : equalBeans.getBeans(); 
		}
		
		public int size() {
			return _innerMap.size();
		}
		
		private Set<Entry<Object, EqualBeans>> entrySet() {
			return _innerMap.entrySet();
		}
	}
	
	private static class EqualBeans {
		private transient WeakReference<Object> _proxy; //surrogate object as the key for the _beanSet
		private transient WeakIdentityMap<Object, Boolean> _beanSet; //different instance of beans equal to each other
		
		public EqualBeans(Object proxy) {
			_proxy = new WeakReference<Object>(proxy);
			_beanSet = new WeakIdentityMap<Object, Boolean>(2);
			_beanSet.put(proxy, Boolean.TRUE);
		}
		
		public void put(Object value) {
			_beanSet.put(value, Boolean.TRUE);
		}
		
		public Set<Object> getBeans() {
			return _beanSet != null ? 
					new IdentityHashSet<Object>(_beanSet.keySet()) : Collections.emptySet();
		}
		
		//return proxy bean(could be migrated or not)
		public Object remove(Object value) {
			_beanSet.remove(value);
			if (_beanSet.isEmpty()) {
				_beanSet = null;
			} else if (System.identityHashCode(_proxy.get()) == System.identityHashCode(value)) {
				//proxy deleted, must migrate proxy
				for(final Iterator<Object> it = _beanSet.keySet().iterator(); it.hasNext(); ) { 
					final Object obj = it.next();
					if (obj != null) {
						_proxy = new WeakReference<Object>(obj); //migrate
						break;
					} else {
						it.remove();
					}
				}
			}
			return _proxy.get();
		}
		
		public boolean isEmpty() {
			return _beanSet == null || _beanSet.isEmpty();
		}
	}
	
	//------ debug dump ------//
	public void dumpLess() {
		dumpCompMap(false);
		dumpBeanMap(false);
		dumpNullMap(false);
		dumpEqualBeansMap();
	}
	public void dump() {
		dumpCompMap(true);
		dumpBeanMap(true);
		dumpNullMap(true);
		dumpEqualBeansMap();
	}
	
	private void dumpBeanMap(boolean dumpNodes) {
		System.out.println("******* _beanMap: *********");
		System.out.println("******* size: "+_beanMap.size());
		for(Object bean : _beanMap.keySet()) {
			System.out.println("bean:"+bean+"------------");
			if(dumpNodes){
				Set<TrackerNode> nodes = _beanMap.get(bean);
				if(nodes!=null){
					for(TrackerNode node : nodes) {
						dumpNodeTree(node, 4);
					}	
				}else{
					System.out.println("NO TrackerNode bound to this bean.");
				}
			}
		}
	}
	
	private void dumpCompMap(boolean dumpNodes) {
		System.out.println("******* _compMap: *********");
		System.out.println("******* size: "+_compMap.size());
		for(Component comp: _compMap.keySet()) {
			System.out.println("comp:"+comp+"------------");
			if(dumpNodes){
				Map<Object, TrackerNode> nodes = _compMap.get(comp);
				for(Entry<Object, TrackerNode> entry : nodes.entrySet()) {
					System.out.println("----field:"+entry.getKey()+"");
					dumpNodeTree(entry.getValue(), 4);
				}
			}
		}
	}

	private void dumpNullMap(boolean dumpNodes) {
		System.out.println("******* _nullMap: *********");
		System.out.println("******* size: "+_nullMap.size());
		for(Object field: _nullMap.keySet()) {
			System.out.println("field:"+field+"------");
			if(dumpNodes){
				Set<TrackerNode> nodes = _nullMap.get(field);
				for(TrackerNode node : nodes) {
					dumpNodeTree(node, 4);
				}
			}
		}
	}

	private void dumpEqualBeansMap() {
		System.out.println("******* _equalBeansMap: *********");
		System.out.println("******* size: "+_equalBeansMap.size());
		
		for(Entry<Object, EqualBeans> entry: _equalBeansMap.entrySet()) {
			System.out.print("proxy:["+entry.getKey());
			System.out.print("], val:["+entry.getValue().getBeans());
			System.out.println("]----");
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
		if(node.getBindings().isEmpty()) return;//don't dump if empty
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
		} else if(binding instanceof ChildrenBinding){
			System.out.println(dumpSpace(spaces)+((ChildrenBinding)binding).getPropertyString()+":"+binding);
		} else if(binding instanceof ReferenceBinding) {
			System.out.println(dumpSpace(spaces)+((ReferenceBinding)binding).getPropertyString()+":"+binding);
		} else{
			System.out.println(dumpSpace(spaces)+":"+binding);
		}
	}
	
	private void dumpPropNameMapping(TrackerNode node, int spaces) {
		if(node.getPropNameMapping().size()==0) return;//don't dump if empty
		System.out.println(dumpSpace(spaces)+"[propertys:");
		for(Entry<Object, Object> entry : node.getPropNameMapping().entrySet()) {
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
