/* TrackerNode.java

	Purpose:
		
	Description:
		
	History:
		Jun 29, 2011 6:37:03 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.tracker;

import java.util.Set;

import org.zkoss.bind.sys.Binding;

/**
 * Tracker Node in a binding dependency graph.
 * @author henrichen
 * @since 6.0.0
 */
public interface TrackerNode {
	/**
	 * Returns all dependent nodes of this node.
	 * @return all precedent nodes of this node.
	 */
	public Set<TrackerNode> getDependents();
	
	/**
	 * Returns all direct dependent nodes of this node.
	 * @return all direct dependent nodes of this node.
	 */
	public Set<TrackerNode> getDirectDependents();
	
	/**
	 * Returns all associate nodes of this node.
	 * @return all associate nodes of this node.
	 */
	public Set<TrackerNode> getAssociates();
	
	/**
	 * Returns the associated TrackerNode of the associated field script of this tracker node.
	 * @param script field script to get the dependent 
	 * @return the linked TrackerNode
	 */
	public TrackerNode getDependent(Object script);
	
	/**
	 * Add a dependent node of the field script.
	 * @param script the field script to link this track node to its dependent nodes.
	 * @param dependent the TrackerNode to be added
	 */
	public void addDependent(Object script, TrackerNode dependent);

	/**
	 * Remove a dependent node of this node per the specified field script.
	 * @param script field script that link to the to be removed dependent tracker node.
	 * @return the removed TrackerNode
	 */
	public TrackerNode removeDependent(Object script);
	
	/** add associated Binding with this node
	 * @param binding
	 */
	public void addBinding(Binding binding);

	/** Returns associated Binding.
	 * 
	 * @return associated Binding.
	 */
	public Set<Binding> getBindings();
	
	/**
	 * Returns associated bean of this TrackerNode.
	 * @return associated bean of this TrackerNode.
	 */
	public Object getBean();
	
	/**
	 * Sets associated bean of this TrackerNode.
	 * @param bean the associated bean.
	 */
	public void setBean(Object bean);
	
	/**
	 * Returns associated field script of this TrackerNode.
	 * @return associated field script of this TrackerNode.
	 */
	public Object getFieldScript();
}
