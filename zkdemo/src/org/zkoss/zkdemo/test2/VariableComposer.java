/* VariableComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Apr 23, 2009 11:18:57 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.util.GenericComposer;

/**
 * Test feature #2778508 (assign as variable beforeCompose)
 * Test feature #2778513 (assign as variable id$composer)
 * @author henrichen
 *
 */
public class VariableComposer extends GenericComposer {
	private String a1 = "A1";
	public String getA1() {
		return a1;
	}
}
