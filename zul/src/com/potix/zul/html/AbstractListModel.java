/* AbstractListModel.java

{{IS_NOTE
	$Id: AbstractListModel.java,v 1.4 2006/02/27 05:14:11 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Aug 18 15:19:43     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import com.potix.zk.ui.UiException;

import com.potix.zul.html.event.ListDataEvent;
import com.potix.zul.html.event.ListDataListener;

/**
 * A skeletal implementation for {@link ListModel}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/02/27 05:14:11 $
 */
abstract public class AbstractListModel implements ListModel {
	private final List _listeners = new LinkedList();

	/** Fires a {@link ListDataEvent} for all registered listener
	 * (thru {@link #addListDataListener}.
	 *
	 * <p>Note: you can invoke this method only in an event listener.
	 */
	protected void fireEvent(int type, int index0, int index1) {
		final ListDataEvent evt = new ListDataEvent(this, type, index0, index1);
		for (Iterator it = _listeners.iterator(); it.hasNext();)
			((ListDataListener)it.next()).onChange(evt);
	}

	//-- ListModel --//
	public void addListDataListener(ListDataListener l) {
		if (l == null)
			throw new NullPointerException();
		_listeners.add(l);
	}
	public void removeListDataListener(ListDataListener l) {
		_listeners.remove(l);
	}
}
