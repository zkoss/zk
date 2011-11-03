/* Idspace.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov 03 12:15:49     2011, Created by benbai

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * Just like DIV tag but implements IdSpace.
 * @since 6.0.0
 * @author benbai
 */
public class Idspace extends Div implements org.zkoss.zk.ui.IdSpace {
	public Idspace() {
		setAttribute("z$is", Boolean.TRUE); //optional but optimized to mean no need to generate z$is since client handles it
	}
}
