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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.LoadBinding;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.bind.sys.tracker.TrackerNode;
import org.zkoss.bind.xel.zel.BindELContext;

/**
 * @author henrichen
 * @since 6.0.0
 */
public class TrackerNodeImpl implements TrackerNode, Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	private final Object _script; //script of this node (e.g. firstname or ['firstname'])
	private final Map<Object, TrackerNode> _dependents; //kid script -> kid TrackerNode
	private final Map<Object, TrackerNode> _brackets; //property -> bracket script
	private final Set<LoadBinding> _bindings; //associated bindings
	private final Set<ReferenceBinding> _refBindings; //associated ReferenceBindings
	private final Set<TrackerNode> _associates; //dependent nodes of this node (e.g. fullname node is dependent node of this firstname node) 
	private transient WeakReference<Object> _bean; //associated bean value

	public TrackerNodeImpl(Object property) {
		_script = property;
		_dependents = new HashMap<Object, TrackerNode>(4);
		_bindings = new HashSet<LoadBinding>(4);
		_refBindings = new HashSet<ReferenceBinding>(2);
		_brackets = new HashMap<Object, TrackerNode>(4);
		_associates = new HashSet<TrackerNode>(4);
	}

	public void addAssociate(TrackerNode node) {
		_associates.add(node);
	}

	public TrackerNode getDependent(Object property) {
		TrackerNode kid = getDependent0(property);
		if (kid == null) { //try bracket
			kid = _brackets.get(property);
		}
		return kid;
	}

	public Set<TrackerNode> getDependents(Object property) {
		LinkedHashSet<TrackerNode> set = new LinkedHashSet<TrackerNode>(5);
		TrackerNode kid = getDependent0(property);
		if (kid != null) {
			set.add(kid);
		}

		TrackerNode node = _brackets.get(property);
		if (node == null && property instanceof String) {
			String prop = (String) property;
			if (BindELContext.isBracket(prop))
				node = _brackets.get(prop.substring(1, prop.length() - 1));
		}
		if (node != null) {
			set.add(node);
		}
		return set;
	}
	
	public Set<TrackerNode> getDependents() {
		return collectDependents0(new HashSet<TrackerNode>());
	}

	private TrackerNode getDependent0(Object script) {
		return _dependents.get(script);
	}

	public void addDependent(Object script, TrackerNode dependent) {
		_dependents.put(script, dependent);
	}

	public void tieProperty(Object property, TrackerNode trackerNode) {
		final Object oldNode = _brackets.get(property);
		if (trackerNode.equals(oldNode)) {
			return;
		}
		if (oldNode != null) {
			_brackets.remove(property);
		}
		for (final Iterator<TrackerNode> it = _brackets.values().iterator(); it.hasNext();) {
			final Object bracket = it.next();
			if (trackerNode.equals(bracket)) {
				it.remove();
				break;
			}
		}
		if (property != null) {
			_brackets.put(property, trackerNode);
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
			_refBindings.add((ReferenceBinding) binding);
		} else {
			_bindings.add((LoadBinding) binding);
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

	//bug# 1: depends-on is not working in nested C->B->A when A changed
	private Set<TrackerNode> collectDependents0(Set<TrackerNode> nodes) {
		final Set<TrackerNode> kids = getDirectDependents();
		nodes.addAll(kids);
		for (TrackerNode kid : kids) {
			((TrackerNodeImpl) kid).collectDependents0(nodes); //recursive
		}
		for (TrackerNode associate : _associates) {
			if (!nodes.contains(associate)) { //avoid endless loop
				nodes.add(associate);
				((TrackerNodeImpl) associate).collectDependents0(nodes); //recursive
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

	public Map<Object, ?> getPropNameMapping() {
		return _brackets;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[bean:").append(getBean()).append(",script:").append(_script).append("]@")
				.append(System.identityHashCode(this));
		return sb.toString();
	}

	public boolean isPropNameNodeMapped(TrackerNode trackerNode) {
		return _brackets == null ? false : _brackets.values().contains(trackerNode);
	}
}
