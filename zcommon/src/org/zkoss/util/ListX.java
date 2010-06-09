/* ListX.java


	Purpose: The extended list interface.
	Description: 
	History:
	 2001/5/9, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

/**
 * The extended list interface. It provides methods
 * to access the entry of each node in the list.
 *
 * <p>Any class implementing
 * this interface guarantees the binding between the entry and the
 * element never changes even after removed.
 *
 * @author tomyeh
 * @see TreeArray
 */
public interface ListX {
	/**
	 * Represents the entry of each node in the list.
	 */
	public interface Entry {
		/**
		 * Gets the element stored in the entry.
		 */
		public Object getElement();
		/**
		 * Sets the entry to store the specified element.
		 */
		public void setElement(Object element);
		/*
		 * Gets the entry next to this one.
		 *
		 * @return the next entry; null if no more
		 */
		public Entry next();
		/*
		 * Gets the entry previous to this one.
		 *
		 * @return the previous entry; null if no more
		 */
		public Entry previous();
		/**
		 * Tests whether an entry is an orphan -- being removed from a list.
		 */
		public boolean isOrphan();
	}

	/**
	 * Gets the entry of the specified index, rather than the element
	 * added by List.add. An entry is an internal representation of an
	 * object that holding the element stored by List.add.
	 *
	 * <p>The caller should consider the returned entry as opaque.
	 * The caller could store it for later use. It is useful when you
	 * want to extend the list's features, such as providing two or
	 * more indexing methods.
	 *
	 * <p>In other words, even if the underlying structure of a list is
	 * changed (e.g., a new element is inserted), the caller holding entries
	 * won't be affected.
	 *
	 * @param index the index from which the entry is retrieved
	 * @return the entry
	 */
	public Entry getEntry(int index);

	/**
	 * Gets the index of the specified entry.
	 *
	 * @param entry the entry to locate
	 * @return the index; -1 if entry is orphan
	 */
	public int indexOfEntry(Entry entry);

	/**
	 * Inserts the sepcified element in front of the specified entry.
	 *
	 * <p>Shifts the element currently at that position (if any)
	 * and any subsequent elements to the right (adds one to their indices).
	 *
	 * @param insertBefore the entry before which an new entry will be
	 * inserted; append is assumed if null
	 * @param element the element to insert
	 * @return the new entry containing the inserted element.
	 */
	public Entry addEntry(Entry insertBefore, Object element);

	/**
	 * Inserts the specified element at the specified position in this list.
	 * It is an enhanced version of List.add -- it returns the entry
	 * containing the element.
	 *
	 * @param index index at which the specified element is to be inserted.
	 * @param element element to be inserted.
	 * @return the new entry that conatining the inserted element.
	 */
	public Entry addEntry(int index, Object element);

	/**
	 * Appends the specified element to the end of this list.
	 * It is an enhanced version of List.add -- it returns the entry
	 * containing the element.
	 *
	 * @param element the element to be inserted.
	 * @return the new entry that conatining the inserted element.
	 */
	public Entry addEntry(Object element);

	/**
	 * Remove the entry from the list. The entry object does not
	 * be deleted, and it is just no longer referenced by the list.
	 *
	 * @param entry the entry returned by getEntry.
	 */
	public void removeEntry(Entry entry);

	/**
	 * Remove the entry at the specified location from the list.
	 * Unlike List.remove(int), it returns the entry being removed.
	 *
	 * @param index the location of the entry to remove
	 * @return the entry being removed
	 */
	public Entry removeEntry(int index);
}
