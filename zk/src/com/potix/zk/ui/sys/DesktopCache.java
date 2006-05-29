/* DesktopCache.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr 18 10:38:22     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import com.potix.zk.ui.Desktop;

/**
 * The cache used to store desktops.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/05/29 04:28:09 $
 */
public interface DesktopCache {
	/** Returns the next available ID which is unique in the whole cache.
	 *
	 * <p>{@link Desktop} uses this method to generate ID automatically.
	 */
	public int getNextId();

	/** Returns the desktop for the specified desktop ID.
	 * @exception ComponentNotFoundException is thrown if the desktop
	 * is not found
	 */
	public Desktop getDesktop(String desktopId);

	/** Adds a desktop to this session.
	 * It must be called when a desktop is created.
	 * <p>Application shall never access this method.
	 */
	public void addDesktop(Desktop desktop);
	/** Removes a desktop from this session.
	 * It must be called when a desktop is remove.
	 * <p>Application shall never access this method.
	 */
	public void removeDesktop(Desktop desktop);

	/** Called when to stop and cleanup this cache.
	 * Once stopped, the caller shall not access it any more.
	 * It cannot be called other than the implementation of
	 * {@link DesktopCacheProvider}.
	 */
	public void stop();
}
