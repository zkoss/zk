/* Tablelayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 14, 2008 10:00:33 AM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.impl.XulElement;

/**
 * Tablelayout lay outs a container as an HTML table whose columns can be
 * specified, and rowspan and colspan of its child can also be specified to 
 * create complex layouts within the table.
 * 
 *  <p>Default {@link #getZclass}: z-table-layout.
 * 
 * @author robbiecheng
 * @since 3.5.0
 */
public class Tablelayout extends XulElement implements org.zkoss.zkmax.zul.api.Tablelayout {
	private int _columns = 1;
	
	public Tablelayout() {
	}
	public String getZclass() {
		return _zclass == null ? "z-table-layout" : _zclass;
	}
	
	/** Sets the number of columns.
	 */
	public void setColumns(int columns) throws WrongValueException {
		if (columns <= 0)
			throw new WrongValueException("Positive only");
		if (_columns != columns) {
			_columns = columns;
			invalidate();
		}
	}

	/** Returns number of rows to span this header.
	 * Default: 1.
	 */
	public int getColumns() {
		return _columns;
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Tablechildren))
			throw new UiException("Unsupported child for Tablelayout: "
					+ child);
		super.beforeChildAdded(child, refChild);
	}
	
	/**
	 * When remove child, layout will be rerender
	 * 
	 * @see ComponentCtrl#onChildRemoved
	 */
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		invalidate();
	}
	
	/**
	 * When add child, layout will be rerender
	 * 
	 * @see ComponentCtrl#onChildAdded
	 */
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		invalidate();
	}
	
}
