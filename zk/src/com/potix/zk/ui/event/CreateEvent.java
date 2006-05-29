/* CreateEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 20:41:25     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

import com.potix.zk.ui.Component;

/**
 * Used to notify a window that all its children are created and initialized.
 * {@link com.potix.zk.ui.sys.UiEngine} post this event to compoents that
 * declares the onCreate handler (either as a method or as in instance definition).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class CreateEvent extends Event {
	public CreateEvent(String name, Component target) {
		super(name, target);
	}
}
