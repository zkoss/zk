/* Paging.java

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

import org.zkoss.zul.ext.Paginal;

/**
 * Paging of long content.
 * 
 * <p>
 * Default {@link #getZclass}: z-paging. (since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Paging extends org.zkoss.zul.impl.api.XulElement, Paginal {

	/**
	 * Returns whether to automatically hide this component if there is only one
	 * page available.
	 * <p>
	 * Default: false.
	 */
	public boolean isAutohide();

	/**
	 * Sets whether to automatically hide this component if there is only one
	 * page available.
	 */
	public void setAutohide(boolean autohide);
}
