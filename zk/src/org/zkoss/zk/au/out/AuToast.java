/* AuToast.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 11 18:33:09 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * The au object for toast.
 *
 * @author rudyhuang
 * @since 9.0.0
 */
public class AuToast extends AuResponse {
	/**
	 * Shows toast at predefined position.
	 *
	 * @param msg the message to show (HTML is accepted)
	 * @param type available types are "info", "warning", "error". default "info".
	 * @param position predefined positions.
	 * @param duration the duration of notification in millisecond. If zero or
	 * negative the notification does not dismiss until user left-clicks outside
	 * of the notification box.
	 * @param closable whether to close notification manually or not. If true there will be a
	 * close button on notification message and won't close until user click the button
	 * or duration time up, default false.
	 * @param animationSpeed animation speed in milliseconds, default 500ms.
	 */
	public AuToast(String msg, String type, String position, int duration, boolean closable, int animationSpeed) {
		super("showToast", new Object[] { msg, type, position, duration, closable, animationSpeed });
	}
}
