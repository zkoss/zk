/* Sessions.java

{{IS_NOTE
	$Id: Sessions.java,v 1.2 2006/02/27 03:54:48 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:33:01     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui;

/**
 * Utilities to access {@link Session}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:48 $
 */
public class Sessions {
	/** Used to the store the session for the current thread. */
	protected static final ThreadLocal _sess = new ThreadLocal();

	protected Sessions() {} //prevent from instantiated

	/** Returns the session for the current thread.
	 */
	public static final Session getCurrent() {
		return (Session)_sess.get();
	}
}
