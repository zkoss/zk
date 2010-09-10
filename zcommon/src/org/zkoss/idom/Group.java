/* Group.java


	Purpose: 
	Description: 
	History:
	2001/10/23 23:50:54, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import java.util.List;
import java.util.Set;
import java.util.Collection;

/**
 * Represents an item might have children. Group is also a item.
 * Developers usually extend new classes from 
 * {@link org.zkoss.idom.impl.AbstractGroup}, rather than
 * implement this interface directly.
 *
 * <p>Design consideration: we don't support removeChildren and setChild
 * or alike, because they can be done easily with List's methods and
 * getAttributeIndex.
 *
 * @author tomyeh
 * @see Item
 * @see Attributable
 * @see Namespaceable
 * @see Binable
 */
public interface Group extends Item {
	/**
	 * Gets all children.
	 *
	 * <p>The returned list is "live". Any modification to it affects
	 * the node. On the other hand, {@link #getElements(String)} returns
	 * a copy.
	 *
	 * <p>Unlike JDOM, it won't coalesce adjacent children automatically
	 * since it might violate the caller's expection about List.
	 * Rather, we provide coalesce to let caller do the merging
	 * explicity.
	 * Note: when building a iDOM tree from a source
	 * (SAXBuilder.build), coalesce() will be inovked automatically.
	 *
	 * <p>Note: not all items supports children. If this item doesn't,
	 * it returns an empty list. And, if any invocation tries to add
	 * vertices to the returned list will cause UnsupportOperationException.
	 */
	public List<Item> getChildren();

	/** Detaches all children and returns them in a list.
	 *
	 * <p>Note: you cannot add children to anther Group by doing
	 * <code>group.addAll(e.getChildren())</code>, because you have to detach
	 * them first. Then, you could use this method:<br>
	 * <code>group.addAll(e.detachChildren());</code>
	 */
	public List<Item> detachChildren();

	/**
	 * Coalesces children if they are siblings with the same type
	 * instances of Textual, Textual.isCoalesceable returns true.
	 *
	 * <p>SAXBuilder.build will do the merging automatically.
	 *
	 * @param recursive true to coalesce all descendants;
	 * false to coalesce only the direct children
	 */
	public int coalesce(boolean recursive);

	/**
	 * Gets the index of the Element-type first child that match the specified
	 * criteria.
	 *
	 * <p>Note: only Element-type children are returned, since others
	 * have no name.
	 *
	 * @param indexFrom the index to start searching from; 0 for beginning
	 * @param namespace the namspace URI if FIND_BY_PREFIX is not specified;
	 * the namespace prefix if FIND_BY_PREFIX specified; null to ingore
	 * @param name the local name if FIND_BY_TAGNAME is not sepcified;
	 * the tag name if FIND_BY_TAGNAME specified; null to ignore
	 * @param mode the serach mode; zero or any combination of
	 * Item.FIND_xxx, except FIND_RECURSIVE
	 * @return the index if found; -1 if not found
	 */
	public int
	getElementIndex(int indexFrom, String namespace, String name, int mode);
	/**
	 * Gets the index of the first Element-type child with the specified name.
	 *
	 * <p>Note: only Element-type children are returned, since others
	 * have no name.
	 *
	 * <p><code><pre>getChildren().add(getElementIndex(0, "pre:name"),
	 *    new Element("pre:another"));</pre></code>
	 *
	 * @param indexFrom the index to start searching from; 0 for beginning
	 * @param tname the tag name (i.e., Namespaceable.getName)
	 * @return the index if found; -1 if not found
	 */
	public int getElementIndex(int indexFrom, String tname);

	/**
	 * Gets the value of the first Element-type child that matches
	 * the giving criteria, with a trimming option.
	 *
	 * @param namespace the namspace URI if FIND_BY_PREFIX is not specified;
	 * the namespace prefix if FIND_BY_PREFIX specified; null to ingore
	 * @param name the local name if FIND_BY_TAGNAME is not sepcified;
	 * the tag name if FIND_BY_TAGNAME specified; null to ignore
	 * @return the value of the first Element-type child ; null if not found
	 */
	public String
	getElementValue(String namespace, String name, int mode, boolean trim);
	/**
	 * Gets the text of the first Element-type child with the tag name,
	 * with a trimming option.
	 *
	 * @param tname the tag name (i.e., Namespaceable.getName)
	 * @return the text of the first Element-type child with
	 * the tag name; null if not found
	 */
	public String getElementValue(String tname, boolean trim);

	/**
	 * Gets the first Element-type child that matches the giving criteria.
	 *
	 * <p>Note: only Element-type children are returned. Depending on
	 * the mode, the searching is usually linear -- take O(n) to complete.
	 *
	 * @param namespace the namspace URI if FIND_BY_PREFIX is not specified;
	 * the namespace prefix if FIND_BY_PREFIX specified; null to ingore
	 * @param name the local name if FIND_BY_TAGNAME is not sepcified;
	 * the tag name if FIND_BY_TAGNAME specified; null to ignore
	 * @param mode the search mode; zero or any combination of FIND_xxx.
	 * @return the found element; null if not found or not supported
	 */
	public Element getElement(String namespace, String name, int mode);
	/**
	 * Gets the first Element-type child with the tag name.
	 * It is the same as childOf(nsURI, lname, null, 0).
	 *
	 * <p>Note: only Element-type children are returned. Also, we did some
	 * optimization for this method so its access time is nearly constant.
	 *
	 * @param tname the tag name (i.e., Namespaceable.getName)
	 * @return the found element; null if not found or not supported
	 */
	public Element getElement(String tname);

	/**
	 * Gets a readonly list of Element-type children that match the giving
	 * criteria.
	 *
	 * <p>Unlike {@link Element#getElementsByTagName}, this method only
	 * returns child elements, excluding grand children and other descedants.
	 *
	 * <p>The returned list is a 'live-facade' of the real ones, so
	 * the performance is good, and any modification to {@link #getChildren}
	 * will affect it.
	 *
	 * <p>Note: only Element-type children are returned. Depending on
	 * the mode, the searching is usually linear -- take O(n) to complete.
	 *
	 * @param namespace the namspace URI if FIND_BY_PREFIX is not specified;
	 * the namespace prefix if FIND_BY_PREFIX specified; null to ingore
	 * @param name the local name if FIND_BY_TAGNAME is not sepcified;
	 * the tag name if FIND_BY_TAGNAME specified; null to ignore
	 * @param mode the search mode; zero or any combination of FIND_xxx.
	 * @return a read-only list containing all matched children;
	 * an empty list if not found or not supported
	 */
	public List<Element> getElements(String namespace, String name, int mode);
	/**
	 * Gets a readonly list of children with the tag name.
	 *
	 * <p>Unlike {@link Element#getElementsByTagName}, this method only
	 * returns child elements, excluding grand children and other descedants.
	 *
	 * <p>The returned list is a 'live-facade' of the real ones, so
	 * the performance is good, and any modification to {@link #getChildren}
	 * will affect it.
	 *
	 * <p>Note: only Element-type children are returned. Also, we did some
	 * optimization for this method so its access time is nearly constant.
	 *
	 * @param tname the tag name (i.e., Namespaceable.getName)
	 */
	public List<Element> getElements(String tname);

	/** Returns a readonly set of names of element children. 
	 * Then, you could use {@link #getElements} to get elements.
	 *
	 * <p>The returned list is a 'live-facade' of the real ones, so
	 * the performance is good, and any modification to {@link #getChildren}
	 * will affect it.
	 *
	 * @see #getElements()
	 */
	public Set<String> getElementNames();
	/** Returns a cloned copy of all element childrens
	 *
	 * <p>Unlike {@link #getChildren} and {@link #getElementNames},
	 * the returned list is NOT a 'live-facade' of the real ones.
	 *
	 * @see #getElementNames()
	 */
	public List<Element> getElements();
}
