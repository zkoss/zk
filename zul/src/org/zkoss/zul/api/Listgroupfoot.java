/* Listgroupfoot.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * GroupFooter serves as a summary listitem of listgroup.
 * 
 * <p>
 * Default {@link #getZclass}: z-listgroupfoot (since 5.0.0)
 * 
 *<p>
 * Note: All the {@link Label} child of this component are automatically applied
 * the group-cell CSS, if you don't want this CSS, you can invoke the
 * {@link Label#setSclass(String)} after the child added.
 * 
 * @author robbiecheng
 * @since 3.5.2
 */
public interface Listgroupfoot extends Listitem {

}
