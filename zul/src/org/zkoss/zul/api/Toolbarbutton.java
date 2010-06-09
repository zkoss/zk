/* Toolbarbutton.java


	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * A toolbar button.
 * 
 * <p>
 * Non-xul extension: Toolbarbutton supports {@link #getHref}. If
 * {@link #getHref} is not null, the onClick handler is ignored and this element
 * is degenerated to HTML's A tag.
 * <p>
 * Default {@link #getZclass}: z-toolbarbutton.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Toolbarbutton extends org.zkoss.zul.api.Button {

}
