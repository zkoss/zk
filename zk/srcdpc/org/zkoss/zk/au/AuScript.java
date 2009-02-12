/* AuScript.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 18 18:12:08     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import org.zkoss.zk.ui.Component;

/**
 * Deprecated: a response to ask the client to execute the specified
 * client scripts.

 * @author tomyeh
 * @deprecated As of release 3.0.0, replaced by {@link org.zkoss.zk.au.out.AuScript}
 */
public class AuScript extends org.zkoss.zk.au.out.AuScript {
	public AuScript(Component depends, String script) {
		super(depends, script);
	}
}
