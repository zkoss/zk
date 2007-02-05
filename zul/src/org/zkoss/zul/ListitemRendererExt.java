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
	 * <p>Example:
<pre><code>
final Listitem item = new Listitem();
item.applyProperties();
return item;
</code></pre>
	 *
	 * <p>Note: DO NOT call {@link Listitem#setParent}.
	 */
	public Listitem newListitem(Listbox listbox);
	/** Create an instance of {@link Listcell} as the first cell of the list item.
	 *
	 * <p>Note: remember to invoke {@link Listcell#applyProperties} to initialize
	 * the properties properly. Also, to invoke {@link Listcell#setParent}
	 * to make it as a child of the specified item.
	 *
	 * <p>Example:
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
	 */
	public Listcell newListcell(Listitem item);
}
