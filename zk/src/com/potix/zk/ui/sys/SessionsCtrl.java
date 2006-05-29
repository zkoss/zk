/* SessionsCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 22:03:45     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import com.potix.zk.ui.Session;
import com.potix.zk.ui.Sessions;

/**
 * An addition interface to {@link Sessions} for implementation.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:28:10 $
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
