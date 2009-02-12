/* Namespaceable.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	2001/10/23 23:52:55, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

/**
 * Represents a class that supports namespace.
 * It is usually implemented by a class that also implements Item or Group.
 * Currently, only Element and Attribute implement it.
 *
 * @author tomyeh
 * @see Item
 * @see Group
 * @see Attributable
 * @see Binable
 */
public interface Namespaceable {
	/**
	 * Gets the namespace. 
	 *
	 * @return the namespace; never null
	 */
	public Namespace getNamespace();
	/**
	 * Sets the namespace. A null namespace will be converted to
	 * Namespace.NO_NAMESPACE.
	 */
	public void setNamespace(Namespace ns);

	/**
	 * Gets the tag name of this item.
	 * The tag name is also called the full qualitifed name -- the name
	 * <i>with</i> the namespace prefix, e.g., prefix:name.
	 *
	 * <p>To get the local name (the name without prefix),
	 * Namespaceable.getLocalName could be used. 
	 */
	public String getTagName();
	/**
	 * Sets the tag name of this item.
	 */
	public void setTagName(String tname);
	/**
	 * Gets the local name of this item.
	 * The local name is the name without prefix.
	 *
	 * <p>To get the tag name (the name with prefix),
	 * Namespaceable.getTagName could be used. 
	 */
	public String getLocalName();
	/**
	 * Sets the local name of this item.
	 */
	public void setLocalName(String lname);
}
