/* PostCreate.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu May 25 11:15:15     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.ext;

/**
 * Implemented by a component if it need the {@link #postCreate} callback
 * when it is created by the ZK loader.
 *
 * <p>A typical example is that macro components use this callback to
 * create its children based on the macro URI.
 *
 * <p>If it is created manually, it is caller's job to invoke postCreate.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface PostCreate {
	/** Invokes after ZK loader applies all properties.
	 */
	public void postCreate();
}
