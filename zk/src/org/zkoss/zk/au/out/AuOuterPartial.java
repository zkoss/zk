/* AuOuterPartial.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 26 10:46:06 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.json.JavaScriptValue;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;

/**
 * A response to ask client to 'outer' the widgets and all its
 * descendants of the associate specified component partially.
 *
 * <p>data[0]: the component or UUID
 * data[1]: the new content
 * data[2]: the redrawn node subid
 *
 * @author rudyhuang
 * @since 9.0.0
 */
public class AuOuterPartial extends AuResponse {
	public AuOuterPartial(Component comp, String content, String subId) {
		super("outerPartial", comp, new Object[] { comp, new JavaScriptValue(content), subId });
	}
}
