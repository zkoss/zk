/* ListgroupRendererExt.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 29 15:22:30     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * Provides additional control to {@link ListitemRenderer} for instantiating
 * {@link Listgroup} and {@link Listgroupfoot}.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public interface ListgroupRendererExt {
	/** Creates an instance of {@link Listgroup} for rendering.
	 * The created component will be passed to {@link ListitemRenderer#render}.
	 *
	 * <p>Note: remember to invoke {@link Listgroup#applyProperties} to initialize
	 * the properties, defined in the component definition, properly.
	 *
	 * <p>If null is returned, the default row is created as follow.
<pre><code>
final Listgroup group = new Listgroup();
group.applyProperties();
return group;
</code></pre>
	 *
	 * <p>Note: DO NOT call {@link Listgroup#setParent}.
	 *
	 * @return the listgroup if you'd like to create it differently, or null
	 * if you want {@link Listbox} to create it for you
	 */
	public Listgroup newListgroup(Listbox listbox);

	/** Creates an instance of {@link Listgroupfoot} for rendering.
	 * The created component will be passed to {@link ListitemRenderer#render}.
	 *
	 * <p>Note: remember to invoke {@link Listgroupfoot#applyProperties} to initialize
	 * the properties, defined in the component definition, properly.
	 *
	 * <p>If null is returned, the default row is created as follow.
<pre><code>
final Listgroupfoot groupfoot = new Listgroupfoot();
groupfoot.applyProperties();
return groupfoot;
</code></pre>
	 *
	 * <p>Note: DO NOT call {@link Listgroupfoot#setParent}.
	 *
	 * @return the listgroupfoot if you'd like to create it differently, or null
	 * if you want {@link Listbox} to create it for you
	 */
	public Listgroupfoot newListgroupfoot(Listbox listbox);
}
