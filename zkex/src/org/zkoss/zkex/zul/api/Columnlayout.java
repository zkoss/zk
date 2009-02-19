/* Columnlayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 09:22:13     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkex.zul.api;

/**
 * A columnlayout lays out a container which can have multiple columns, and each
 * column may contain one or more panel.<br>
 * Use Columnlayout need assign width (either present or pixel) on every
 * Columnchildren, or we cannot make sure about layout look.
 * 
 * <p>
 * Default {@link org.zkoss.zkex.zul.Columnlayout#getZclass}: z-column-layout.
 * 
 * @author gracelin
 * @since 3.5.0
 */
public interface Columnlayout extends org.zkoss.zul.impl.api.XulElement {

}
