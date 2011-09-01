/* NonFellow.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 16 11:27:04     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

/**
 * Decorates {@link org.zkoss.zk.ui.Component} to denote that
 * the component won't become a fellow of the ID space it belongs.
 *
 * <p>By default, if {@link org.zkoss.zk.ui.Component#setId} is called,
 * the component is added to the ID space ({@link org.zkoss.zk.ui.IdSpace}).
 * However, if this interface is implemented, it won't be added to the
 * ID space. In other words, {@link org.zkoss.zk.ui.IdSpace#getFellow}
 * will never return this component.
 * 
 * @author tomyeh
 * @since 3.0.0
 * @see org.zkoss.zk.ui.IdSpace
 */
public interface NonFellow {
}
