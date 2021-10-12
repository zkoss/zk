/* Notification.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 16 17:22:31 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.au.out.AuNotification;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

/**
 * Utilities to send Notification to the client.
 *
 * Note: this class makes accessing 'client-side' more directly.
 * Methods such as {@link #show(String)} will not encode the strings passed into them,
 * thus the formatting of messages at 'client-side' is allowed.
 * User input should be escaped carefully.
 *
 * @author rudyhuang
 * @since 9.0.0
 */
public class Notification {
	/**
	 * Notification type: information
	 */
	public static final String TYPE_INFO = "info";

	/**
	 * Notification type: warning
	 */
	public static final String TYPE_WARNING = "warning";

	/**
	 * Notification type: error
	 */
	public static final String TYPE_ERROR = "error";


	/**
	 * Displays a message.
	 *
	 * @param msg the message to show
	 * @param type available types are "info", "warning", "error"
	 * @param ref the referenced component, null to be based on browser window
	 * @param position predefined positions.
	 * <p> Available options are:
	 * <ul>
	 *  <li><b>before_start</b><br/> the message appears above the anchor, aligned to the left.</li>
	 *  <li><b>before_center</b><br/> the message appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the message appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the message appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the message appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the message appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the message appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the message appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the message appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the message appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the message appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the message appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the message overlaps the anchor, with anchor and message aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the message overlaps the anchor, with anchor and message aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the message overlaps the anchor, with anchor and message aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the message appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the message appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the message at the horizontal position of the mouse cursor.</li>
	 * </ul></p>
	 * @param duration the duration of notification in millisecond. If zero or
	 * negative the notification does not dismiss until user left-clicks outside
	 * of the notification box.
	 * @param closable whether to close notification manually or not. If true there will be a
	 * close button on notification message and won't close until user click the button
	 * or duration time up, default false.
	 */
	public static void show(String msg, String type, Component ref, String position, int duration, boolean closable) {
		Execution exec = Executions.getCurrent();
		Page page = ref != null ? ref.getPage() : null;
		if (page == null && exec instanceof ExecutionCtrl)
			page = ((ExecutionCtrl) exec).getCurrentPage();
		if (type == null)
			type = TYPE_INFO;
		exec.addAuResponse(new AuNotification(msg, type, page, ref, position, duration, closable));
	}

	/**
	 * Displays a message.
	 * @param msg the message to show
	 * @param type available types are "info", "warning", "error"
	 * @param ref the referenced component, null to be based on browser window
	 * @param x the horizontal position of the notification, aligned at top-left (in pixel)
	 * @param y the vertical position of the notification, aligned at top-left (in pixel)
	 * @param duration the duration of notification in millisecond. If zero or
	 * negative the notification does not dismiss until user left-clicks outside
	 * of the notification box.
	 * @param closable whether to close notification manually or not. If true there will be a
	 * close button on notification message and won't close until user click the button
	 * or duration time up, default false.
	 */
	public static void show(String msg, String type, Component ref, int x, int y, int duration,
	                        boolean closable) {
		Execution exec = Executions.getCurrent();
		Page page = ref != null ? ref.getPage() : null;
		if (page == null && exec instanceof ExecutionCtrl)
			page = ((ExecutionCtrl) exec).getCurrentPage();
		if (type == null)
			type = TYPE_INFO;
		exec.addAuResponse(new AuNotification(msg, type, page, ref, x, y, duration, closable));
	}

	/**
	 * Displays a message.
	 * @param msg the message to show
	 * @param type available types are "info", "warning", "error"
	 * @param ref the referenced component, null to be based on browser window
	 * @param x the horizontal position of the notification, aligned at top-left (in pixel)
	 * @param y the vertical position of the notification, aligned at top-left (in pixel)
	 * @param duration the duration of notification in millisecond. If zero or
	 * negative the notification does not dismiss until user left-clicks outside
	 * of the notification box.
	 */
	public static void show(String msg, String type, Component ref, int x, int y, int duration) {
		show(msg, type, ref, x, y, duration, false);
	}

	/**
	 * Displays a message.
	 * @param msg the message to show
	 * @param type available types are "info", "warning", "error"
	 * @param ref the referenced component, null to be based on browser window
	 * @param position predefined positions.
	 * <p> Available options are:
	 * <ul>
	 *  <li><b>before_start</b><br/> the message appears above the anchor, aligned to the left.</li>
	 *  <li><b>before_center</b><br/> the message appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the message appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the message appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the message appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the message appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the message appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the message appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the message appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the message appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the message appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the message appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the message overlaps the anchor, with anchor and message aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the message overlaps the anchor, with anchor and message aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the message overlaps the anchor, with anchor and message aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the message overlaps the anchor, with anchor and message aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the message overlaps the anchor, with anchor and message aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the message appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the message appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the message at the horizontal position of the mouse cursor.</li>
	 * </ul></p>
	 * @param duration the duration of notification in millisecond. If zero or
	 * negative the notification does not dismiss until user left-clicks outside
	 * of the notification box.
	 */
	public static void show(String msg, String type, Component ref, String position, int duration) {
		show(msg, type, ref, position, duration, false);
	}

	/**
	 * Shows a message at the right side of the given component.
	 * @param msg the message to show
	 * @param ref the referenced component, null to be based on browser window
	 * @param closable whether to close notification manually or not. If true there will be a
	 * close button on notification message and won't close until user click the button
	 * or duration time up, default false.
	 */
	public static void show(String msg, Component ref, boolean closable) {
		show(msg, null, ref, null, -1, closable);
	}

	/**
	 * Shows a message at the right side of the given component.
	 * @param msg the message to show
	 * @param ref the referenced component, null to be based on browser window
	 */
	public static void show(String msg, Component ref) {
		show(msg, null, ref, null, -1, false);
	}

	/**
	 * Shows a message at the center of the browser window.
	 * @param msg the message to show
	 */
	public static void show(String msg) {
		show(msg, null, null, null, -1, false);
	}

	/**
	 * Shows a message at the center of the browser window.
	 * @param msg the message to show
	 * @param closable whether to close notification manually or not. If true there will be a
	 * close button on notification message and won't close until user click the button
	 * or duration time up, default false.
	 */
	public static void show(String msg, boolean closable) {
		show(msg, null, null, null, -1, closable);
	}
}
