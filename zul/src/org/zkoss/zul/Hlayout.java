/* Hlayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 6, 2010 11:34:20 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * A horizontal layout
 *<p>Default {@link #getZclass}: z-hlayout.
 * @author jumperchen
 * @since 5.0.4
 */
public class Hlayout extends Layout {
	public String getZclass() {
		return _zclass == null ? "z-hlayout" : _zclass;
	}
}
