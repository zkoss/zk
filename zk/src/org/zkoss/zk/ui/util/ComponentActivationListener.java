/* ComponentActivationListener.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 10:31:30     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Component;

/**
 * Used to notify an object stored in a component, when the session
 * is going to be deactivated or has been activated.
 *
 * <p>When a session is going to be deactivate, it checks every
 * variable of a component to see whether this interface is implemented.
 * If implemented, {@link #willPassivate} will be called.
 * Similarly, {@link #didActivate} is called if the session has
 * been activated.
 * 
 * @author tomyeh
 * @since 3.6.2
 */
public interface ComponentActivationListener {
	/** Called when a session has just been activated
	 * (and its value has been deserialized).
	 */
	public void didActivate(Component comp);
	/** Called when a session is about to be passivated
	 * (and then serialize its value).
	 */
	public void willPassivate(Component comp);
}
