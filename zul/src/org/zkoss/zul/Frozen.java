/* Frozen.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 2, 2009 9:54:29 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.XulElement;

/**
 * A frozen component to represent a frozen column or row in grid, like MS Excel. 
 * <p>Default {@link #getZclass}: z-frozen.
 * @author jumperchen
 * @since 5.0.0
 */
public class Frozen extends XulElement implements org.zkoss.zul.api.Frozen {

	private int _columns;
	private int _start;
	
	public void setStart(int start) {
		if (_start != start) {
			_start = start;
			smartUpdate("start", _start);
		}
	}
	
	public int getStrat() {
		return _start;
	}
	/**
	 * Sets the number of columns to freeze.(from left to right)
	 * @param columns positive only
	 */
	public void setColumns(int columns) {
		if (columns < 0)
			throw new WrongValueException("Positive only");
		if (_columns != columns) {
			_columns = columns;
			smartUpdate("columns", _columns);
		}
	}

	/**
	 * Returns the number of columns to freeze.
	 * <p>Default: 0
	 */
	public int getColumns() {
		return _columns;
	}

	/**
	 * Sets the number of rows to freeze.(from top to bottom)
	 * 
	 * <p>Note: this feature is reserved and not yet implemented.
	 * @param rows positive only
	 */
	public void setRows(int rows) {
	}

	/**
	 * Returns the number of rows to freeze.
	 * <p>Default: 0
	 */
	public int getRows() {
		return 0;
	}

	//super//
	public String getZclass() {
		return _zclass == null ? "z-frozen" : _zclass;
	}
		
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		if (_columns > 0)
			renderer.render("columns", _columns);		
		if (_columns > 0)
			renderer.render("start", _start);		
	}
}
