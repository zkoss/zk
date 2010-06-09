/* CheckableTreeArray.java


	Purpose: 
	Description: 
	History:
	2001/10/05 14:43:05, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * The checkable TreeArray. It extends TreeArray such that deriving
 * classes could add validation when an element is added, removed or set.
 * It is also useful to maintain the modification flag.
 *
 * @author tomyeh
 */
public abstract class CheckableTreeArray extends TreeArray {
	protected CheckableTreeArray() {
	}
	protected CheckableTreeArray(Collection c) {
		super(c);
	}

	//-- deriving to override --//
	/** Called each time an new element is about being added into the array.
	 *
	 * <p>Deriving classes usually put checking codes here.
	 * And, throws exception if failure and nothing will be affected.
	 *
	 * @param newElement the element to be added
	 * @param followingElement the elment that will 'follow' the new element.
	 * In other words, newElement will be inserted <b>before</b>
	 * followingElement. If null, it means newElement is appended at the end
	 */
	protected void onAdd(Object newElement, Object followingElement) {
	}
	/** Called each time an element is about being assigned into the array
	 * and replace an existence one (by ListIterator.set).
	 *
	 * <p>Deriving classes usually put checking codes here.
	 * And, throws exception if failure and nothing will be affected.
	 *
	 * @param newElement the element to be added
	 * @param replaced the element to be replaced
	 */
	protected void onSet(Object newElement, Object replaced) {
	}
	/** Called each time an element is about being removed from the array.
	 * Deriving classes usually put checking codes here.
	 * And, throws exception if failure.
	 */
	protected void onRemove(Object element) {
	}

	//-- TreeArray --//
	protected RbEntry newEntry(Object element) {
		return new CkEntry(element);
	}
	protected class CkEntry extends RbEntry {
		protected CkEntry(Object element) {
			super(element);
		}
		public void setElement(Object element) {
			onSet(element, this.element);
			super.setElement(element);
		}
	}
	protected RbEntry insert(RbEntry insertBefore, final RbEntry p) {
		onAdd(p.element, insertBefore != null ? insertBefore.element: null);
		return super.insert(insertBefore, p);
	}
	protected void delete(RbEntry p) {
		onRemove(p.element);
		super.delete(p);
	}
	public void clear() {
		for (final Iterator iter = iterator(); iter.hasNext();)
			onRemove(iter.next());
		super.clear();
	}
}
