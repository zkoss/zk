/* AuScript.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:29:32     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to execute the specified client scripts.
 *
 * <p>data[0]: the client script codes (i.e., JavaScript codes)
 *
 * <p>It is generally not a good idea to use {@link AuScript} send
 * JavaScript codes to the client, unless it doesn't depend on widgets.
 * On the other hand, it is better to use {@link AuInvoke} since
 * it invokes thru the widget such that it is easy to customize.
 *
 * @author tomyeh
 * @see AuInvoke
 * @since 3.0.0
 */
public class AuScript extends AuResponse {
	/**
	 * @param depends the component that this script depends on.
	 * Or null if this script shall always execute.
	 */
	public AuScript(Component depends, String script) {
		super("script", depends, script);
	}
}
