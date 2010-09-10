/* Attributable.java


Purpose: 
Description: 
History:
C2001/10/23 12:38:28, reate, Tom M. Yeh

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import java.util.List;

/**
 * Represents a class that has attributes.
 * It is usually implemented by a class that also implements Item or Group.
 * Currently, only Element implements it.
 *
 * <p>Design consideration: Be as similar to Group as possible.
 *
 * @author tomyeh
 * @see Item
 * @see Attribute
 */
public interface Attributable {
	/**
	 * Returns all attributes of this object.
	 *
	 * <p>The returned list is "live". Any modification to it affects
	 * the object that owns the attributes.
	 *
	 * <p>If the new added attribute has the same tag name as
	 * that of any existent attribute, DOMException is thrown.
	 * Thus, it is, sometimes, more convenient to ue setAttribute.
	 *
	 * <p>Naming reason: we don't call it getAttributes() to avoid
	 * the name conflict with Node.getAttributes().
	 *
	 * @return an empty list if no attribute at all
	 */
	public List<Attribute> getAttributeItems();

	/**
	 * Gets the index of the first attribute that matches
	 * the specified criteria.
	 *
	 * @param indexFrom the index to start searching from; 0 for beginning
	 * @param namespace the namspace URI if FIND_BY_PREFIX is not specified;
	 * the namespace prefix if FIND_BY_PREFIX specified; null to ingore
	 * @param name the local name if FIND_BY_TAGNAME is not sepcified;
	 * the tag name if FIND_BY_TAGNAME specified; null to ignore
	 * @param mode the serach mode; zero or any combination of Item.FIND_xxx
	 * @return the index if found; -1 if not found
	 */
	public int
	getAttributeIndex(int indexFrom, String namespace, String name, int mode);
	/**
	 * Gets the index of the attribute with the giving local name.
	 *
	 * @param indexFrom the index to start searching from; 0 for beginning
	 * @param tname the tag name (i.e., {@link Attribute#getName}) --
	 * consists of the prefix and the local name
	 * @return the index if found; -1 if not found
	 */
	public int getAttributeIndex(int indexFrom, String tname);

	/**
	 * Gets the value of the first attribute that matches
	 * the giving criteria, or null if not found.
	 *
	 * <p>According to Section 3.3.3 of XML 1.0 spec, the value is normalized,
	 * including trimmed.
	 *
	 * @param namespace the namspace URI if FIND_BY_PREFIX is not specified;
	 * the namespace prefix if FIND_BY_PREFIX specified; null to ingore
	 * @param name the local name if FIND_BY_TAGNAME is not sepcified;
	 * the tag name if FIND_BY_TAGNAME specified; null to ignore
	 * @return the value of the attribute; null if not found
	 */
	public String getAttributeValue(String namespace, String name, int mode);
	/** Returns the value of the attribute of the specified tag name,
	 * or null if not specified.
	 *
	 * <p>Note: unlike W3C's getAttribute, which returns empty if not specified,
	 * this method returns null if not specified.
	 */
	public String getAttributeValue(String tname);

	/**
	 * Gets the first attribute that matches the specified criteria.
	 *
	 * <p>The name is a bit strange because we have to avoid name conflicts
	 * with org.w3c.dom.Node.
	 *
	 * @param namespace the namspace URI if FIND_BY_PREFIX is not specified;
	 * the namespace prefix if FIND_BY_PREFIX specified; null to ingore
	 * @param name the local name if FIND_BY_TAGNAME is not sepcified;
	 * the tag name if FIND_BY_TAGNAME specified; null to ignore
	 * @param mode the serach mode; zero or any combination of Item.FIND_xxx
	 * @return the index if found; -1 if not found
	 */
	public Attribute getAttributeItem(String namespace, String name, int mode);
	/**
	 * Gets the attribute with the tag name.
	 *
	 * <p>The name is a bit strange because we have to avoid name conflicts
	 * with org.w3c.dom.Node.
	 *
	 * @param tname the tag name (i.e., {@link Attribute#getName}) --
	 * consists of the prefix and the local name
	 * @return null if not found
	 */	
	public Attribute getAttributeItem(String tname);

	/**
	 * Gets a list of attributes of the specified criteria.
	 *
	 * @param namespace the namspace URI if FIND_BY_PREFIX is not specified;
	 * the namespace prefix if FIND_BY_PREFIX specified; null to ingore
	 * @param name the local name if FIND_BY_TAGNAME is not sepcified;
	 * the tag name if FIND_BY_TAGNAME specified; null to ignore
	 * @param mode the serach mode; zero or any combination of Item.FIND_xxx
	 * @return null if not found
	 */
	public List<Attribute> getAttributes(String namespace, String name, int mode);

	/**
	 * Adds the giving attribute.
	 * If there is any existent one with the same tag name,
	 * it will be replaced. If not, the new attribute will be appended.
	 *
	 * @param attr the new attribute to add
	 * @return the attribute being replaced; null if no one is replaced
	 */
	public Attribute setAttribute(Attribute attr);
	/**
	 * Sets the value of the attribute with the giving tag name.
	 * If the attribute doesn't exist, a new attribute will be created
	 * and added.
	 *
	 * <p>Note: it looks similar to Attribute(String, String), <i>but</i>
	 * this method requires the <i>tag</i> name.
	 *
	 * @param tname the tag name (i.e., Attribute.getName)
	 * @param value the new value.
	 * @return the attribute being replaced; null if no one is replaced
	 */
	public Attribute setAttributeValue(String tname, String value);

	/** @deprecated As of release 6.0.0, it always returns false.
	 */
	public boolean isAttributeModificationAware();
	/** @deprecated As of release 6.0.0, it does nothing.
	 */
	public void setAttributeModificationAware(boolean aware);
}
