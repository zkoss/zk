/* RawId.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Nov 29 08:59:37     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.ext;

/**
 * Decorates {@link com.potix.zk.ui.Component} to denote that
 * its UUID ({@link com.potix.zk.ui.Component#getUuid} must be
 * the same as {@link com.potix.zk.ui.Component#getId},
 * if com.potix.zk.ui.Component#setId} is ever called.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:28:04 $
 */
public interface RawId {
}
