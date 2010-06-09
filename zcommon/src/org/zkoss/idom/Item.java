/* Item.java


	Purpose: 
	Description: 
	History:
	2001/10/21 15:54:59, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import org.w3c.dom.Node;

import org.zkoss.xml.Locator;

/**
 * Represents an item (aka., node) of a iDOM tree. A iDOM tree is not necessary
 * also a W3C/DOM tree. However, in iDOM's implement, it is
 * because all Vertices also implement related interface, such as Node.
 *
 * <p>Some vertices, currently only Element, might support attributes
 * by implementing Attributable.
 *
 * <p>Due to the implementation of both Item and W3C/DOM's interfaces,
 * many methods seem redudant (e.g., parent vs. getParentNode, and
 * children vs. getChildNodes).
 * Caller could use them interchangeably . However, it is
 * suggested to use Item's API instead of Node's, because, like JDOM,
 * Item's API is based Java collection classes and more consistent
 * (from my point of view). The W3C/DOM API is used to work with utilities
 * that work only with W3C/DOM.
 *
 * <p>Be carefult that some methods look similar, but behave different.
 * Refer to package.html.
 *
 * @author tomyeh
 * @see Attributable
 * @see Binable
 */
public interface Item {
	/** Indicates the searching is based on regular expression
	 * (upon the name argument).
	 * If not specified, exact-match is required.
	 */
	public static final int FIND_BY_REGEX = 0x0001;
	/** Indicates the searching is case insensitive.
	 * This flag is ignored if FIND_BY_REGEX is specified.
	 */
	public static final int FIND_IGNORE_CASE = 0x0002;
	/** Indicates the name argument is a tag name
	 * rather than local name.
	 */
	public static final int FIND_BY_TAGNAME = 0x0004;
	/** Indicates the namespace argument is a prefix rather
	 * than URI.
	 */
	public static final int FIND_BY_PREFIX = 0x0008;
	/** Indicates the searching looks for all descendants.
	 * If not specified, only the children (not children of children)
	 * is searched.
	 */
	public static final int FIND_RECURSIVE = 0x0100;

	/**
	 * Tests whether this item is read-only.
	 * Note: A item is read-only if the read-only flag is set ({@link #setReadonly})
	 * or any of its ancestor is read-only (i.e., the read-only flag is set).
	 */
	public boolean isReadonly();
	/**
	 * Sets the read-only flag of this item. It causes this item
	 * and all its descendants read-only, see {@link #isReadonly}.
	 */
	public void setReadonly(boolean readonly);

	/**
	 * Gets the name of the item.
	 * For vertices that support namespace (implements Namespaceable),
	 * it is the same as getTagName.
	 *
	 * @see Namespaceable#getTagName
	 */
	public String getName();
	/**
	 * Sets the name of the item.
	 * For vertices that support namespace (implements Namespaceable),
	 * it is the same as setTagName.
	 *
	 * @exception DOMException with NOT_SUPPORTED_ERR if this item
	 * doesn't allow to change the name, e.g., Document and DocType
	 *
	 * @see Namespaceable#setTagName
	 */
	public void setName(String name);

	/**
	 * Gets the text of this item, or null if it is neither {@link Textual}
	 * nor {@link Element}.
	 * For Element, the text is the catenation of all its textual
	 * children, including Text, CDATA, and Binary.
	 *
	 * <p>Besides String-type value, some item, e.g., Binary, allows
	 * any type of objects. Caller could test whether a item implements
	 * the Binable interface, and use Binable.getValue instead.
	 * For binable vertices, getText is actually getValue().toString().
	 *
	 * <p>The returned value is neither trimmed nor normalized.
	 */
	public String getText();
	/**
	 * Sets the text of this item.
	 *
	 * @exception DOMException with NOT_SUPPORTED_ERR if this item
	 * doesn't allow to change the value, e.g., Document and Element
	 */
	public void setText(String obj);

	/**
	 * Gets the document that owns this item.
	 * The owning document is the first document in its ancestor.
	 * For DOM, the document can only be the root, so the owning documents
	 * of vertices in a DOM tree are all the same.
	 */
	public Document getDocument();

	/**
	 * Detach this item from its parent.
	 *
	 * <p>Because each item can belong to at most one parent at a time, it
	 * is important to detach it first, before added to another tree -- even
	 * if it is the same tree/parent. 
	 *
	 * <p>It has the similar effect as:<br>
	 * getParent().getChildren().remove(this).
	 *
	 * <p>Naming reason: we don't call this method as getChildren() to be
	 * compatible with the naming style of Attributable.attributes -- which
	 * is limited to org.w3c.dom.Attr.getAttributes.
	 * Also, it doesn't have the setter and it is "live", so it
	 * 'seem' better to call it getChildren().
	 *
	 * @return this item
	 */
	public Item detach();

	/**
	 * Gets the parent item.
	 */
	public Group getParent();

	/**
	 * Gets the locator of this item.
	 * @return the locator; null if not available (default)
	 */
	public Locator getLocator();
	/**
	 * Sets the locator of this item.
	 *
	 * <p>Unlike other methods, it won't change the modification flag.
	 *
	 * @param loc the locator; null if not available
	 */
	public void setLocator(Locator loc);

	/**
	 * Clones this item. Unlike other objects, it does a deep cloning.
	 * Also, the returned object is detached by default.
	 * The readonly flags are cleaned. If preserveModified is false,
	 * all modification flags of the returned object are cleaned, too.
	 */
	public Item clone(boolean preserveModified);

	/**
	 * Tests whether this item (or any of its children) is modified,
	 * i.e., the modification flag is set.
	 *
	 * <p>IDOM is smart enough to set the modification flag only if
	 * it is really modified -- i.e., assigning a different value (not just
	 * calling any setter).
	 *
	 * <p>When an object is serialized, the modification flag is reset.
	 * Also, an object generated by {@link #clone(boolean)} with
	 * preservedModified==false has also no modification flag set.
	 */
	public boolean isModified();
	/**
	 * Clears the modification flag of this item and all its children
	 * if includingDescendant is true.
	 *
	 * <p>Unlike other methods, it doesn't affected by the read-only flag.
	 *
	 * @param includingDescendant whether to clear the modified flags
	 * of descendants
	 */
	public void clearModified(boolean includingDescendant);
	/**
	 * Sets the modification flag. Unlike {@link #clearModified}, it won't set
	 * children's modification flags, rather it sets its parent's modification
	 * flag.
	 *
	 * <p>The modifiaction flag is maintain automatically, so you rarely needs
	 * to call this method.
	 *
	 * <p>Unlike other methods, it doesn't affected by the read-only flag.
	 */
	public void setModified();

	//-- Internal Methods --//
	/**
	 * Sets the parent item.
	 *
	 * <p><b><i>DO NOT</i></b> call this method. It is used internally.
	 * Instead, use detach or thru getChildren().
	 */
	public void setParent(Group parent);
}
