/* SessionsCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 22:03:45     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

/**
 * An addition interface to {@link Sessions} for implementation.
 *
 * @author tomyeh
 */
public class SessionsCtrl extends Sessions {
	protected SessionsCtrl() {} //prevent from instantiation

	/** Sets the session for the current thread.
	 * Called only internally.
	 */
	public static final void setCurrent(Session sess) {
		_sess.set(sess);
	}
	/** Returns the current {@link SessionCtrl}.
	 */
	public static final SessionCtrl getCurrentCtrl() {
		return (SessionCtrl)getCurrent();
	}
}
