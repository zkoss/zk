/* ComponentCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:06:56     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.metainfo.ComponentDefinition;

/**
 * An addition interface to {@link Component} that is used for
 * implementation.
 *
 * <p>Application developers shall never access any of this methods.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface ComponentCtrl {
	/** Sets the component definition.
	 */
	public void setDefinition(ComponentDefinition compdef);
}
