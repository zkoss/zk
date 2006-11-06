/* AuScript.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:29:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import org.zkoss.zk.ui.Component;

/**
 * A response to ask the client to execute the specified script.
 *
 * <p>data[0]: the JavaScript codes
 *
 * @author tomyeh
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
