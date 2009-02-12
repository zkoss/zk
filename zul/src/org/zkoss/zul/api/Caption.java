/* Caption.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A header for a {@link Groupbox}. It may contain either a text label, using
 * {@link #setLabel}, or child elements for a more complex caption.
 * <p>
 * Default {@link #getZclass}: z-caption.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Caption extends org.zkoss.zul.impl.api.LabelImageElement {
}
