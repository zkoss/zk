/* Annotation.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 25 16:05:13     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Map;

/**
 * The common interface extended by all annotation types.
 * An annotation consists of a name ({@link #getName}) and a map of attributes
 * {@link #getAttribute}.
 *
 * <p>To get the annotation associated with a component, use
 * {@link org.zkoss.zk.ui.sys.ComponentCtrl#getAnnotation(String)}.
 * To get the annotation associated with a component's property,
 * use {@link org.zkoss.zk.ui.sys.ComponentCtrl#getAnnotation(String, String)}
 *
 * @author tomyeh
 * @see org.zkoss.zk.ui.sys.ComponentCtrl#getAnnotation(String)
 * @see org.zkoss.zk.ui.sys.ComponentCtrl#getAnnotation(String, String)
 * @see org.zkoss.zk.ui.sys.ComponentCtrl#getAnnotations()
 * @see org.zkoss.zk.ui.sys.ComponentCtrl#getAnnotations(String)
 */
public interface Annotation extends java.io.Serializable {
	/** Returns the name of this annotation.
	 *
	 * <p>For each declaration, there is at most one annotation with the same
	 * name.
	 */
	public String getName();
	/** Returns the map of attributes (String name, String value) (never null).
	 * The returned map is read-only.
	 */
	public Map getAttributes();
	/** Returns the attribute of the given name, or null if not found.
	 */
	public String getAttribute(String name);
}
