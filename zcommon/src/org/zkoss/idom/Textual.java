/* Textual.java


	Purpose: 
	Description: 
	History:
	2001/10/24 20:23:58, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

/**
 * Represents an object that is mainly for storing "text".
 * It is usually implemented by a class that also implements Item.
 *
 * <p>A "text" is usually a string (e.g., Text, Comment and CDATA) but
 * could be any object (e.g., Binary).
 *
 * <p>The getText method of some parent, e.g., Element, catenates
 * the text of its children if they implement this interface and
 * Textual.isPartOfParentText returns true.
 *
 * <p>Note: the class that implement this interface must have a constructor
 * with a single argument whose type is String. The split method will
 * invoke it to create a new instance.
 *
 * @author tomyeh
 * @see Item
 */
public interface Textual {
	/**
	 * Splits at the specified offset into two Textual objects.
	 * The new textual object is inserted right after this one.
	 *
	 * @return the new textual object; null if offset is no less than length
	 */
	public Textual split(int offset);

	/**
	 * Returns true if this textual object is part of the parent's text.
	 * Currently, only Element.getText uses it.
	 */
	public boolean isPartOfParentText();

	/**
	 * Returns true if this textual object is allowed to be coalesced with
	 * its siblings with the same type (class).
	 * It is used by Group.coalesce.
	 */
	public boolean isCoalesceable();
}
