/* SessionCtrl.java

{{IS_NOTE
	$Id: SessionCtrl.java,v 1.7 2006/04/18 07:38:33 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 15:08:34     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.ComponentNotFoundException;

/**
 * Additional interface of {@link com.potix.zk.ui.Session} for implementation.
 * <p>Note: applications shall never access this interface.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.7 $ $Date: 2006/04/18 07:38:33 $
 */
public interface SessionCtrl {
	/** Called when the session is destroyed.
	 * <p>Application shall never access this method.
	 */
	public void onDestroyed();

	/** Returns whether this session is invalidated
	 * (i.e., {@link com.potix.zk.ui.Session#invalidate} was called).
	 */
	public boolean isInvalidated();
	/** Really invalidates the session.
	 * <p>Application shall never access this method.
	 */
	public void invalidateNow();
}
