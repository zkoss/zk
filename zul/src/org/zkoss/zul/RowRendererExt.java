/* RowRendererExt.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Mar  8 10:57:52     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
	/** Creates an instance of {@link Row} for rendering.
	 * The created component will be passed to {@link RowRenderer#render}.
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
	/** Create a component as the first cell of the row.
	 *
	 * <p>Note: remember to invoke {@link Component#applyProperties} to
	 * initialize the properties, defined in the component definition, properly,
	 * if you create an instance instead of returning null.
	 *
	 * <p>Note: DO NOT call {@link Row#setParent}.
	 *
	 * <p>If null is returned, the default cell is created as follow.
<pre><code>
final Label cell = new Label();
cell.applyProperties();
return cell;
</code></pre>
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

	/** Called to see whether to detach the cell adde by {@link #newCell}.
	 *
	 * <p>To simplify the implementation of {@link RowRenderer#render},
	 * {@link Grid}, by default, detached the cell before calling
	 * {@link RowRenderer#render}.
	 *
	 * <p>If you want the cell to being detached before
	 * {@link RowRenderer#render} (the default behavior), just return true.
	 * If you return false, your implementation of  {@link RowRenderer#render}
	 * must be aware of the existence of the first cell (of an unloaded row).
	 */
	public boolean shallDetachOnRender(Component cell);
}
