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
import com.potix.zk.ui.sys.WebAppCtrl;

/**
 * A skeletal implementation of {@link Session} and {@link SessionCtrl}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
abstract public class AbstractSession
implements Session, SessionCtrl {
	private transient WebApp _wapp;
	private boolean _invalid;

	protected AbstractSession(WebApp wapp) {
		if (wapp == null)
			throw new IllegalArgumentException("null");
		_wapp = wapp;
	}

	public final WebApp getWebApp() {
		return _wapp;
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

	/** Notification that the session is about to be passivated
	 * (aka., serialized).
	 */
	protected void sessionWillPassivate() {
		((WebAppCtrl)_wapp).sessionWillPassivate(this);
	}
	/** Notification that the session has just been activated
	 * (aka., deserialized).
	 */
	protected void sessionDidActivate(WebApp wapp) {
		if (wapp == null) throw new IllegalArgumentException("null");
		//if (_wapp != null) throw new IllegalStateException("recall to existent instance?");
		_wapp = wapp;
		((WebAppCtrl)_wapp).sessionDidActivate(this);
	}
}
