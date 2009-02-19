/* AuScript.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:29:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
 * <p>It is usually not a good idea to use {@link AuScript} send codes
 * to the client,
 * since it makes it difficult to customize the visual representation
 * of a component.
 * Rather, it is better to use {@link AuInvoke} instead.
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
