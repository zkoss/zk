/* ListitemRendererExt.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Feb  5 10:10:12     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * Provides additional control to {@link ListitemRenderer}.
 *
 * @author tomyeh
 * @see ListitemRenderer
 */
public interface ListitemRendererExt {
	/** Creates an instance of {@link Listitem} for rendering.
	 * The created component will be passed to {@link ListitemRenderer#render}.
	 *
	 * <p>Note: remember to invoke {@link Listitem#applyProperties} to initialize
	 * the properties properly.
	 *
	 * <p>If null is returned, the default list item is created as follow.
<pre><code>
final Listitem item = new Listitem();
item.applyProperties();
return item;
</code></pre>
	 *
	 * <p>Note: DO NOT call {@link Listitem#setParent}.
	 *
	 * @return the list item if you'd like to create it differently, or null
	 * if you want {@link Listbox} to create it for you
	 */
	public Listitem newListitem(Listbox listbox);
	/** Create an instance of {@link Listcell} as the first cell of the list item.
	 *
	 * <p>Note: remember to invoke {@link Listcell#applyProperties} to initialize
	 * the properties properly. Also, to invoke {@link Listcell#setParent}
	 * to make it as a child of the specified item.
	 *
	 * <p>If null is returned, the default list cell is created as follow.
<pre><code>
final Listcell cell = new Listcell();
cell.applyProperties();
return cell;
</code></pre>
	 *
	 * <p>Note: DO NOT call {@link Listcell#setParent}.
	 *
	 * @param item the list item. It is the same as that is returned
	 * by {@link #newListitem}
	 * @return the list cell if you'd like to create it differently, or null
	 * if you want {@link Listbox} to create it for you
	 */
	public Listcell newListcell(Listitem item);

	/** Called to see whether to detach the list cell adde by {@link #newListcell}.
	 *
	 * <p>To simplify the implementation of {@link ListitemRenderer#render},
	 * {@link Listbox}, by default, detached the list cell before calling
	 * {@link ListitemRenderer#render}.
	 *
	 * <p>If you want the cell to being detached before
	 * {@link ListitemRenderer#render} (the default behavior), just return true.
	 * If you return false, your implementation of  {@link ListitemRenderer#render}
	 * must be aware of the existence of the first cell (of an unloaded list item).
	 */
	public boolean shallDetachOnRender(Listcell cell);
}
