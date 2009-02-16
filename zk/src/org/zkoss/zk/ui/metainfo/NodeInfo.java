/* NodeInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 26 17:33:45     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * Represents a node of the ZUML tree.
 * It is an abstract class while the concrete classes include
 * {@link PageDefinition} and {@link ComponentInfo}.
 * The root must be an instance of {@link PageDefinition}
 * and the other nodes must be instances of {@link ComponentInfo},
 * {@link ZScript}, {@link VariablesInfo}, or {@link AttributesInfo}.
 *
 * <p>Note:it is not thread-safe.
 * 
 * @author tomyeh
 */
abstract public class NodeInfo {
	/** A list of {@link ComponentInfo} and {@link ZScript}. */
	/*pacakge*/ List _children = new LinkedList();

	public NodeInfo() {
	}

	/** Returns the page definition, or null if not available.
	 */
	abstract public PageDefinition getPageDefinition();
	/** Returns the parent, or null if no parent.
	 */
	abstract public NodeInfo getParent();

	/** Returns the evaluator reference (never null).
	 *
	 * <p>This method is used only for implementation only.
	 * @since 3.0.0
	 */
	abstract protected EvaluatorRef getEvaluatorRef();

	/** Adds a zscript child.
	 */
	public void appendChild(ZScript zscript) {
		appendChildDirectly(zscript);
	}
	/** Adds a variables child.
	 */
	public void appendChild(VariablesInfo variables) {
		appendChildDirectly(variables);
	}
	/** Adds a custom-attributes child.
	 */
	public void appendChild(AttributesInfo custAttrs) {
		appendChildDirectly(custAttrs);
	}
	/** Adds a {@link ComponentInfo} child.
	 * @since 2.4.0
	 */
	public void appendChild(ComponentInfo compInfo) {
		compInfo.setParent(this); //it will call back appendChildDirectly
	}

	/** Removes a zscript child.
	 * @return whether the child is removed successfully.
	 */
	public boolean removeChild(ZScript zscript) {
		return removeChildDirectly(zscript);
	}
	/** Removes a variables child.
	 * @return whether the child is removed successfully.
	 */
	public boolean removeChild(VariablesInfo variables) {
		return removeChildDirectly(variables); 
	}
	/** Removes a custom-attributes child.
	 * @return whether the child is removed successfully.
	 */
	public boolean removeChild(AttributesInfo custAttrs) {
		return removeChildDirectly(custAttrs); 
	}
	/** Removes a {@link ComponentInfo} child.
	 *
	 * <p>Call {@link ComponentInfo#setParent} instead.
	 * @return whether the child is removed successfully.
	 * @since 2.4.0
	 */
	public boolean removeChild(ComponentInfo compInfo) {
		if (compInfo != null && removeChildDirectly(compInfo)) {
			compInfo.setParentDirectly(null);
			return true;
		}
		return false;
	}
	
	/** Adds a child.
	 * <p>Note: it does NOT maintain {@link ComponentInfo#getParent}.
	 */
	/*pacakge*/ void appendChildDirectly(Object child) {
		if (child == null)
			throw new IllegalArgumentException("child required");
		_children.add(child);
	}
	/** Removes a child.
	 * <p>Note: it does NOT maintain {@link ComponentInfo#getParent}.
	 */
	/*package*/ boolean removeChildDirectly(Object child) {
		return _children.remove(child);
	}

	/** Returns a list of children.
	 * Children include instances of {@link ComponentInfo}, {@link ZScript}
	 * {@link VariablesInfo}, or {@link AttributesInfo}.
	 *
	 * <p>Note: the returned list is live but it is not a good idea
	 * to modify it directly,
	 * because, unlike {@link org.zkoss.zk.ui.Component}, it doesn't maintain
	 * {@link NodeInfo#getParent}. Thus, it is better to invoke
	 * {@link #appendChild(ComponentInfo)} and {@link #removeChild(ComponentInfo)}
	 * instead.
	 */
	public List getChildren() {
		return _children;
	}
}
