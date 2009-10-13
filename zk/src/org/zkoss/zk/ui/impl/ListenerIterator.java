/* ListenerIterator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun  4 16:12:35     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.List;
import java.util.Iterator;

/**
 * Used to iterate the event listener.
 *
 * <p>It resolves a bug of LinkedList:
 * LinkedList's hasNext() and hasPrevious() don't check
 * concurrent-modification.
 * Thus, if the 2nd last listener is removed, hasNext() simply return
 * false (and the last listener is ignored) rather than throwing
 * ConcurrentModificationException. Refer to Bug 1730532.
 *
 * @author tomyeh
 */
public class ListenerIterator implements Iterator {
	private final List _org;
	private int _orgsz;
	private final Iterator _it;

	public ListenerIterator(List list) {
		_org = list;
		_orgsz = list.size();
		_it = list.iterator();
	}
	public boolean hasNext() {
		if (_orgsz != _org.size())
			throw new java.util.ConcurrentModificationException();
		return _it.hasNext();
	}
	public Object next() {
		return _it.next();
	}
	/** Removes the listener been interated (available since 3.6.3)
	 */
	public void remove() {
		_it.remove();
		_orgsz--;
	}
}
