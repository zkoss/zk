/* Borderlayout.java

 {{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 09:22:13     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkex.zul.api;

/**
 * A border layout lays out a container, arranging and resizing its components
 * to fit in five regions: north, south, east, west, and center. Each region may
 * contain no more than one component, and is identified by a corresponding
 * constant: <code>NORTH</code>, <code>SOUTH</code>, <code>EAST</code>,
 * <code>WEST</code>, and <code>CENTER</code>. When adding a component to a
 * container with a border layout, use one of these five constants, for example:
 * 
 * <pre>
 *  &lt;borderlayout&gt;
 *  &lt;north margins=&quot;1,5,1,1&quot; size=&quot;20%&quot; splittable=&quot;true&quot; collapsible=&quot;true&quot; minsize=&quot;100&quot; maxsize=&quot;400&quot;&gt;
 *  &lt;div&gt;
 *  North
 *  &lt;/div&gt;
 *  &lt;/north&gt;
 *  &lt;west size=&quot;25%&quot; splittable=&quot;true&quot; autoscroll=&quot;true&quot;&gt;
 *  &lt;div&gt;
 *  West
 *  &lt;/div&gt;
 *  &lt;/west&gt;
 *  &lt;center flex=&quot;true&quot;&gt;
 *  &lt;div&gt;
 *  Center
 *  &lt;/div&gt;
 *  &lt;/center&gt;
 *  &lt;east size=&quot;25%&quot; collapsible=&quot;true&quot; onOpen='alert(self.id + &quot; is open :&quot; +event.open)'&gt;
 *  &lt;div&gt;
 *  East
 *  &lt;/div&gt;
 *  &lt;/east&gt;
 *  &lt;south size=&quot;50%&quot; splittable=&quot;true&quot;&gt;
 *  &lt;div&gt;
 *  south
 *  &lt;/div&gt;
 *  &lt;/south&gt;
 *  &lt;/borderlayout&gt;
 * 
 * </pre>
 * 
 * <p>
 * Default {@link org.zkoss.zkex.zul.Borderlayout#getZclass}: z-border-layout. (since 3.5.0)
 * 
 * @author jumperchen
 * @since 3.0.0
 */
public interface Borderlayout extends org.zkoss.zk.ui.api.HtmlBasedComponent {

	public North getNorthApi();

	public South getSouthApi();

	public West getWestApi();

	public East getEastApi();

	public Center getCenterApi();

	/**
	 * Re-size this layout component.
	 */
	public void resize();

}
