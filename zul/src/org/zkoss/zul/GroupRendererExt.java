/* GroupRendererExt.java

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
 * Provides additional control to {@link RowRenderer} for instantiating
 * {@link Group} and {@link Groupfoot}.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public interface GroupRendererExt {
	/** Creates an instance of {@link Group} for rendering.
	 * The created component will be passed to {@link RowRenderer#render}.
	 *
	 * <p>Note: remember to invoke {@link Group#applyProperties} to initialize
	 * the properties, defined in the component definition, properly.
	 *
	 * <p>If null is returned, the default row is created as follow.
<pre><code>
final Group group = new Group();
group.applyProperties();
return group;
</code></pre>
	 *
	 * <p>Note: DO NOT call {@link Group#setParent}.
	 *
	 * @return the group if you'd like to create it differently, or null
	 * if you want {@link Grid} to create it for you
	 */
	public Group newGroup(Grid grid);

	/** Creates an instance of {@link Groupfoot} for rendering.
	 * The created component will be passed to {@link RowRenderer#render}.
	 *
	 * <p>Note: remember to invoke {@link Groupfoot#applyProperties} to initialize
	 * the properties, defined in the component definition, properly.
	 *
	 * <p>If null is returned, the default row is created as follow.
<pre><code>
final Groupfoot groupfoot = new Groupfoot();
groupfoot.applyProperties();
return groupfoot;
</code></pre>
	 *
	 * <p>Note: DO NOT call {@link Groupfoot#setParent}.
	 *
	 * @return the groupfoot if you'd like to create it differently, or null
	 * if you want {@link Grid} to create it for you
	 */
	public Groupfoot newGroupfoot(Grid grid);
}
