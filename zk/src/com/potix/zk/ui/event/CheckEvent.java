/* CheckEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 00:28:22     2005, Created by tomyeh@potix.com
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
 * Represents an event cause by user's check a state at the client.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:28:01 $
 */
public class CheckEvent extends Event {
	private final boolean _checked;

	/** Constructs a check-relevant event.
	 * @param checked whether
	 */
	public CheckEvent(String name, Component target, boolean checked) {
		super(name, target);
		_checked = checked;
	}
	/** Returns whether the state is checked.
	 */
	public final boolean isChecked() {
		return _checked;
	}
}
