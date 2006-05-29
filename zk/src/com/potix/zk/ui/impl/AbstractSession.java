/* AbstractSession.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 15:07:18     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import com.potix.zk.ui.Session;
import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.sys.SessionCtrl;

/**
 * A skeletal implementation of {@link Session} and {@link SessionCtrl}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
abstract public class AbstractSession implements Session, SessionCtrl {
	private final WebApp _webapp;
	private boolean _invalid;

	protected AbstractSession(WebApp webapp) {
		if (webapp == null)
			throw new IllegalArgumentException("null");
		_webapp = webapp;
	}

	public final WebApp getWebApp() {
		return _webapp;
	}

	public final void invalidate() {
		_invalid = true;
	}
	public final boolean isInvalidated() {
		return _invalid;
	}

	/** Default: does nothing. */
	public void onDestroyed() {
	}
}
