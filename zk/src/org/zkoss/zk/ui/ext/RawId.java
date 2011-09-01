/* RawId.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 29 08:59:37     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

/**
 * Decorates {@link org.zkoss.zk.ui.Component} to denote that
 * the UUID ({@link org.zkoss.zk.ui.Component#getUuid} must be
 * the same as the ID ({@link org.zkoss.zk.ui.Component#getId}),
 * if the applicaion assigns an ID for it.
 * Thus, if org.zkoss.zk.ui.Component#setId} is called,
 * both UUID and ID are changed.
 *
 * @author tomyeh
 */
public interface RawId {
}
