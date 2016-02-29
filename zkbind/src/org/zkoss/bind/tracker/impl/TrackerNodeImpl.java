/* TrackerNodeImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 25, 2011 9:24:41 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.tracker.impl;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.LoadBinding;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.sys.tracker.TrackerNode;

/**
 * @author henrichen
 * @since 6.0.0
 */
public class TrackerNodeImpl implements TrackerNode,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	private final Object _script; //script of this node (e.g. firstname or ['firstname'])
	private Map<Object, TrackerNode> _dependents; //kid script -> kid TrackerNode
	private Map<Object, Object> _brackets; //property -> bracket script
	private Set<LoadBinding> _bindings; //associated bindings
	private Set<ReferenceBinding> _refBindings; //associated ReferenceBindings
	private Set<TrackerNode> _associates; //dependent nodes of this node (e.g. fullname node is dependent node of this firstname node) 
	private transient WeakReference<Object> _bean; //associated bean value
	
	public TrackerNodeImpl(Object property) {
		_script = property;
		_dependents = Collections.emptyMap();
		_bindings = Collections.emptySet();
		_refBindings = Collections.emptySet();
		_brackets = Collections.emptyMap();
		_associates = Collections.emptySet();
	}
	
	public void addAssociate(TrackerNode node) {
		 if (_associates.isEmpty()) {
			 //collection is empty map, create singleton set to minimize memory footprint
			 _associates = Collections.singleton(node);
		 } else if (_associates.size() == 1) {
			 //collection is singleton map, create hashset
			 Set<TrackerNode> oldAssociates = _associates;
			 _associates = new HashSet<TrackerNode>(4);
			 _associates.addAll(oldAssociates);
			 _associates.add(node);
		 } else {
			 _associates.add(node);
		 }
	}
	
	public TrackerNode getDependent(Object property) {
		TrackerNode kid = getDependent0(property);
		if (kid == null) { //try bracket
			final Object script = _brackets.get(property);
			if (script != null) {
				kid = getDependent0(script);
			}
		}
		return kid;
	}
	
	private TrackerNode getDependent0(Object script) {
		return _dependents.get(script); 
	}

	public void addDependent(Object script, TrackerNode dependent) {
		if (_dependents.isEmpty()) {
			//collection is empty map, create singleton map to minimize memory footprint
			_dependents = Collections.singletonMap(script, dependent);
		} else if (_dependents.size() == 1) {
			//collection is singleton map, create hash map
			Map<Object, TrackerNode> oldDependents = _dependents;
			_dependents = new HashMap<Object, TrackerNode>(4);
			_dependents.putAll(oldDependents);
			_dependents.put(script, dependent);
		} else {
			_dependents.put(script, dependent);
		}
	}
	
	public void tieProperty(Object property, Object script) {
		final Object oldscript = _brackets.get(property);
		if (script.equals(oldscript)) {
			return;
		}
		if (oldscript != null) {
			if (_brackets.size() == 1 && _brackets.containsKey(property)) {
				//_brackets is probably singletonMap, than initialize map as empty
				_brackets = Collections.emptyMap();
			} else if (_brackets.size() > 1) {
				//brackets is HashMap, remove property
				_brackets.remove(property);
			}
		}
		for (final Iterator<Object> it = _brackets.values().iterator(); it.hasNext();) {
			final Object bracket = it.next();
			if (script.equals(bracket)) {
				if (_brackets.size() == 1) {
					//brackets is probably singleton map, 
					_brackets = Collections.emptyMap();
				} else {
					it.remove();
				}
				break;
			}
		}
		if (property != null) {
			if (_brackets.isEmpty()) {
				//create singleton map to reduce memory footprint
				_brackets = Collections.singletonMap(property, script);
			} else if (_brackets.size() == 1) {
				//collection is probably singleton map, create new hash map
				Map<Object, Object> oldBrackets = _brackets;
				_brackets = new HashMap<Object, Object>(4);
				_brackets.putAll(oldBrackets);
				_brackets.put(property, script);
			} else {
				_brackets.put(property, script);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.zkoss.bind.tracker.TrackerNode#removeDependent(java.lang.String)
	 */
	public TrackerNode removeDependent(Object script) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addBinding(Binding binding) {
		if (binding instanceof ReferenceBinding) {
			
			if (_refBindings.isEmpty()) {
				//create singleton set to reduce memory footprint
				_refBindings = Collections.singleton((ReferenceBinding)binding);
			} else if (_refBindings.size() == 1) {
				//collection is probably singleton set, create new has set
				Set<ReferenceBinding> oldRefBindings = _refBindings;
				_refBindings = new HashSet<ReferenceBinding>(2);
				_refBindings.addAll(oldRefBindings);
				_refBindings.add((ReferenceBinding)binding);
			} else {
				_refBindings.add((ReferenceBinding)binding);
			}
			
			
		} else {
			if (_bindings.isEmpty()) {
				//create singleton set to reduce memory footprint
				_bindings = Collections.singleton((LoadBinding)binding);
			} else if (_bindings.size() == 1) {
				//collection is probably singleton set, create new has set
				Set<LoadBinding> oldBindinds = _bindings;
				_bindings = new HashSet<LoadBinding>(4);
				_bindings.addAll(oldBindinds);
				_bindings.add((LoadBinding)binding);
			} else {
				_bindings.add((LoadBinding)binding);	
			}
		}
	}
	
	public Set<Binding> getBindings() {
		final Set<Binding> bindings = new HashSet<Binding>();
		bindings.addAll(getLoadBindings());
		bindings.addAll(getReferenceBindings());
		
		return bindings;
	}
	
	public Set<ReferenceBinding> getReferenceBindings() {
		return _refBindings;
	}
	
	public Set<LoadBinding> getLoadBindings() {
		return _bindings;
	}

	public Set<TrackerNode> getDependents() {
		return collectDependents0(new HashSet<TrackerNode>());
	}
	
	//bug# 1: depends-on is not working in nested C->B->A when A changed
	private Set<TrackerNode> collectDependents0(Set<TrackerNode> nodes) {
		final Set<TrackerNode> kids = getDirectDependents();
		nodes.addAll(kids);
		for(TrackerNode kid : kids) {
			((TrackerNodeImpl)kid).collectDependents0(nodes); //recursive
		}
		for(TrackerNode associate : _associates) {
			if (!nodes.contains(associate)) { //avoid endless loop
				nodes.add(associate);
				((TrackerNodeImpl)associate).collectDependents0(nodes); //recursive
			}
		}
		return nodes;
	}

	public Set<TrackerNode> getDirectDependents() {
		return new HashSet<TrackerNode>(_dependents.values());
	}
	
	public Set<TrackerNode> getAssociates() {
		return _associates;
	}

	public Object getBean() {
		Object bean = _bean == null ? null : _bean.get();
		if (bean == null && _bean != null) { //Help GC
			setBean(null);
		}
		return bean;
	}

	public void setBean(Object bean) {
		_bean = bean == null ? null : new WeakReference<Object>(bean);
	}
	
	public Object getFieldScript() {
		return _script;
	}
	
	public Map<Object, Object> getPropNameMapping() {
		return _brackets;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[bean:").append(getBean()).append(",script:").append(_script).append("]@").append(System.identityHashCode(this));
		return sb.toString();
	}
}
