/* DesktopActivationListener.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 10:45:12     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Desktop;

/**
 * Used to notify an object stored in a desktop, when the session
 * is going to be deactivated or has been activated.
 *
 * <p>When a session is going to be deactivate, it checks every
 * variable of a desktop to see whether this interface is implemented.
 * If implemented, {@link #willPassivate} will be called.
 * Similarly, {@link #didActivate} is called if the session has
 * been activated.
 * 
 * @author tomyeh
 * @since 3.6.2
 */
public interface DesktopActivationListener {
	/** Called when a session has just been activated
	 * (and its value has been deserialized).
	 */
	public void didActivate(Desktop desktop);
	/** Called when a session is about to be passivated
	 * (and then serialize its value).
	 */
	public void willPassivate(Desktop desktop);
}
