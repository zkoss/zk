/* DesktopCtrl.java

{{IS_NOTE
	$Id: DesktopCtrl.java,v 1.7 2006/04/18 07:38:33 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jul 29 08:47:19     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;

/**
 * An addition interface to {@link com.potix.zk.ui.Desktop}
 * for implementation.
 *
 * <p>Note: applications shall never access this interface.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.7 $ $Date: 2006/04/18 07:38:33 $
 */
public interface DesktopCtrl {
	/** Returns the request queue.
	 */
	public RequestQueue getRequestQueue();

	/** Returns the next available ID which is unique in the whole desktop.
	 *
	 * <p>{@link Page} uses this method to generate ID automatically.
	 */
	public int getNextId();

	/** Adds a component to this page.
	 * <p>It is used internally and developers shall not invoke it
	 * explicityly.
	 */
	public void addComponent(Component comp);
	/** Removes a component to this page.
	 * <p>It is used internally and developers shall not invoke it
	 * explicityly.
	 */
	public void removeComponent(Component comp);

	/** Adds a page to this desktop.
	 * It must be called when a page is created.
	 *
	 * <p>This is one of the only few method you could access
	 * before activating an execution.
	 */
	public void addPage(Page page);
	/** Removes a page from this desktop.
	 * <p>NOTE: once a page is removed, you can NOT add it back.
	 * You shall just GC it.
	 */
	public void removePage(Page page);

	/** Sets the execution (used to represent a lock).
	 * Called only internally (by UIEngine's implementation to activate
	 * an execution).
	 */
	public void setExecution(Execution exec);
}
