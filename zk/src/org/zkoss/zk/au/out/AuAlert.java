/* AuAlert.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:26:35     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask client to show an alert.
 *
 * <p>data[0]: the alert message
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuAlert extends AuResponse {
	public AuAlert(String message) {
		super("alert", message); //component-independent
	}
	/**
	 * @param message the message to display.
	 * @param title the title of the message box
	 * @since 5.0.3
	 */
	public AuAlert(String message, String title) {
		super("alert", new String[] {message, title}); //component-independent
	}
	/**
	 * @param message the message to display.
	 * @param title the title of the message box
	 * @param icon the icon to show. It could null,
	 "QUESTION", "EXCLAMATION", "INFORMATION", "ERROR", "NONE".
	 * If null, "ERROR" is assumed
	 * @since 5.0.3
	 */
	public AuAlert(String message, String title, String icon) {
		super("alert", new String[] {message, title, icon}); //component-independent
	}
}
