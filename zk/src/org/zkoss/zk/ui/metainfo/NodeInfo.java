/* NodeInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 26 17:33:45     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a node of the ZUML tree.
 * It is an abstract class while the concrete classes include
 * {@link PageDefinition} and {@link ComponentInfo}.
 * The root must be an instance of {@link PageDefinition}
 * and the other nodes must be instances of {@link ComponentInfo},
 * {@link ZScript}, {@link VariablesInfo}, or {@link AttributesInfo}.
 * 
 * @author tomyeh
 */
abstract public class NodeInfo {
	/** A list of {@link ComponentInfo} and {@link ZScript}. */
	private final List _children = new LinkedList(),
		_roChildren = Collections.unmodifiableList(_children);

	/** Returns the page definition.
	 */
	abstract public PageDefinition getPageDefinition();
	/** Returns the parent.
	 */
	abstract public NodeInfo getParent();

	/** Adds a zscript child.
	 */
	public void appendChild(ZScript zscript) {
		appendChild((Object)zscript);
	}
	/** Adds a variables child.
	 */
	public void appendChild(VariablesInfo variables) {
		appendChild((Object)variables);
	}
	/** Adds a custom-attributes child.
	 */
	public void appendChild(AttributesInfo custAttrs) {
		appendChild((Object)custAttrs);
	}
	/** Adds a {@link ComponentInfo} child.
	 *
	 * <p>Call {@link ComponentInfo#setParent} instead.
	 */
	/*package*/ void appendChild(ComponentInfo compInfo) {
		appendChild((Object)compInfo);
	}

	/** Removes a zscript child.
	 * @return whether the child is removed successfully.
	 */
	public boolean removeChild(ZScript zscript) {
		return removeChild((Object)zscript);
	}
	/** Removes a variables child.
	 * @return whether the child is removed successfully.
	 */
	public boolean removeChild(VariablesInfo variables) {
		return removeChild((Object)variables); 
	}
	/** Removes a custom-attributes child.
	 * @return whether the child is removed successfully.
	 */
	public boolean removeChild(AttributesInfo custAttrs) {
		return removeChild((Object)custAttrs); 
	}
	/** Removes a {@link ComponentInfo} child.
	 *
	 * <p>Call {@link ComponentInfo#setParent} instead.
	 * @return whether the child is removed successfully.
	 */
	/*package*/ boolean removeChild(ComponentInfo compInfo) {
		return removeChild((Object)compInfo);
	}

	/** Adds a child.
	 */
	private void appendChild(Object child) {
		if (child == null)
			throw new IllegalArgumentException("child required");
		synchronized (_children) {
			_children.add(child);
		}
	}
	/** Removes a child.
	 */
	private boolean removeChild(Object child) {
		synchronized (_children) {
			return _children.remove(child);
		}
	}

	/** Returns a readonly list of children.
	 * Children includes another {@link ComponentInfo}, {@link ZScript}
	 * {@link VariablesInfo}, or {@link AttributesInfo}.
	 */
	public List getChildren() {
		return _roChildren;
	}
}
