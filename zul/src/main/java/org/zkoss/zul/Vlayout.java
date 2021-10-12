/* Vlayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 6, 2010 12:46:39 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * A vertical layout
 *<p>Default {@link #getZclass}: z-vlayout.
 * @author jumperchen
 * @since 5.0.4
 */
public class Vlayout extends Layout {
	public String getZclass() {
		return _zclass == null ? "z-vlayout" : _zclass;
	}
}
