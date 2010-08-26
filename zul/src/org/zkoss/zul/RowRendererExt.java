/* RowRendererExt.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar  8 10:57:52     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;

/**
 * Provides additional control to {@link RowRenderer}.
 * 
 * @author tomyeh
 */
public interface RowRendererExt {
	/** Creates an instance of {@link Row} that will be attached to grid.
	 * The created component will be passed to {@link RowRenderer#render}
	 * to append the required information to show a row of the data.
	 *
	 * <p>Note: remember to invoke {@link Row#applyProperties} to initialize
	 * the properties, defined in the component definition, properly.
	 *
	 * <p>If null is returned, the default row is created as follow.
<pre><code>
final Row row = new Row();
row.applyProperties();
return row;
</code></pre>
	 *
	 * <p>Note: DO NOT call {@link Row#setParent}.
	 *
	 * @return the row if you'd like to create it differently, or null
	 * if you want {@link Grid} to create it for you
	 */
	public Row newRow(Grid grid);
	/** Create a component that will be attached to the <b>unloaded</b> row.
	 * By unloaded row we mean the row that is not loaded with the data
	 * retreived from the model. That is, {@link RowRenderer#render}
	 * is not called yet.
	 *
	 * <p>Notice that this callback shall generate an empty cell,
	 * rather than showing the data retrieved from the model.
	 * The showing of the data from model shall be done
	 * in {@link RowRenderer#render}.
	 *
	 * <p>If null is returned, the default cell is created as follows.
<pre><code>
final Label cell = new Label();
cell.applyProperties();
return cell;
</code></pre>
	 *
	 * <p>Note: remember to invoke {@link Component#applyProperties} to
	 * initialize the properties, defined in the component definition, properly,
	 * if you create an instance instead of returning null.
	 *
	 * <p>Note: DO NOT call {@link Row#setParent}.
	 *
	 * <p>Note: DO NOT call {@link Component#setParent}.
	 * Don't create cells for other columns.
	 *
	 * @param row the row. It is the same as that is returned
	 * by {@link #newRow}
	 * @return the cell if you'd like to create it differently, or null
	 * if you want {@link Grid} to create it for you
	 */
	public Component newCell(Row row);

	/** Returned by {@link #getControls} to indicate
	 * that the list cells added by {@link #newCell} must be
	 * detached before calling {@link RowRenderer#render}.
	 *
	 * <p>Default: true.<br/>
	 * If this interface is not specified, this flag is assumed
	 * to be specified.
	 *
	 * <p>If you don't specify this flag, the implementation of 
	 * {@link RowRenderer#render} must be aware of the existence of
	 * the first cell (of the passed row).
	 */
	public static final int DETACH_ON_RENDER = 0x0001;
	/** Returns how a grid shall render the live data.
	 *
	 * <p>Note: if this interface is not implemented, {@link #DETACH_ON_RENDER}
	 * is assumed.
	 *
	 * @return a combination of {@link #DETACH_ON_RENDER} or 0
	 * to indicate how to render the live data.
	 */
	public int getControls();
}
