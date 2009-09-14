/* NamespaceActivationListener.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  4 12:18:22     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.scripting;

/**
 * @deprecated As of release 5.0.0, use
 * {@link org.zkoss.zk.ui.util.ComponentActivationListener}
 * or {@link org.zkoss.zk.ui.util.PageActivationListener} instead.
 *
 * <p>Used to notify a variable stored in a namespace, when the namespace
 * is going to be deactivated or has been activated.
 *
 * <p>When a namespace is going to be deactivate, it checks every
 * variable to see whether this interface is implemented.
 * If implemented, {@link #willPassivate} will be called.
 * Similarly, {@link #didActivate} is called if the namespace has
 * been activated.
 * 
 * @author tomyeh
 * @since 3.6.2
 */
public interface NamespaceActivationListener {
	/** Called when a namespace has just been activated
	 * (and its value has been deserialized).
	 */
	public void didActivate(Namespace ns);
	/** Called when a namespace is about to be passivated
	 * (and then serialize its value).
	 */
	public void willPassivate(Namespace ns);
}
