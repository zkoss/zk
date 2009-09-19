/* WebManagerActivationListener.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  6 10:56:48     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

/**
 * A listener that will be invoked when the Web manager is created (aka.,
 * activated).
 *
 * <p>To register a listener, use {@link WebManager#addActivationListener}.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public interface WebManagerActivationListener {
	/** Called after WebManager is created.
	 */
	public void didActivate(WebManager webman);
}
