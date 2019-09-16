package org.zkoss.zk.ui.util;

import static org.zkoss.zk.ui.util.Clients.NOTIFICATION_TYPE_INFO;

import org.zkoss.lang.Library;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.out.AuToast;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;

/**
 * Utilities to send Toast to the client.
 *
 * Note: this class makes accessing 'client-side' more directly.
 * Methods such as {@link #show(String)} will not encode the strings passed into them,
 * thus the formatting of messages at 'client-side' is allowed.
 * User input should be escaped carefully.
 *
 * <h3>Custom Attributes</h3>
 * <dl>
 * <dt>org.zkoss.zk.ui.util.Toast.animationSpeed</dt>
 * <dd>Specifies the duration of Toast opening and closing animation in milliseconds.</br>
 * The default value is 500(ms).</dd>
 * </dl>
 *
 * @author rudyhuang
 * @since 9.0.0
 */
public class Toast {
	private static Integer _animationSpeed;

	private static void response(AuResponse response) {
		Executions.getCurrent().addAuResponse(response);
	}

	private static int getAnimationSpeed() {
		if (_animationSpeed == null)
			_animationSpeed = Library.getIntProperty("org.zkoss.zk.ui.util.Toast.animationSpeed", 500);
		return _animationSpeed;
	}

	/**
	 * Display a toast notification message.
	 *
	 * @param msg the message to show (HTML is accepted)
	 * @param type available types are "info", "warning", "error". default "info".
	 * @param position predefined positions. Available options
	 * <table border="1">
	 *   <tr>
	 *     <th></th>
	 *     <th>left</th>
	 *     <th>center</th>
	 *     <th>right</th>
	 *   </tr>
	 *   <tr>
	 *     <td>top</td>
	 *     <td>top_left</td>
	 *     <td>top_center</td>
	 *     <td>top_right</td>
	 *   </tr>
	 *   <tr>
	 *     <td>middle</td>
	 *     <td>middle_left</td>
	 *     <td>middle_center</td>
	 *     <td>middle_right</td>
	 *   </tr>
	 *   <tr>
	 *     <td>bottom</td>
	 *     <td>bottom_left</td>
	 *     <td>bottom_center</td>
	 *     <td>bottom_right</td>
	 *   </tr>
	 * </table>
	 * default "top_center".
	 * @param duration the duration of notification in millisecond. If zero or
	 * negative the notification does not dismiss until user left-clicks outside
	 * of the notification box.
	 * @param closable whether to close notification manually or not. If true there will be a
	 * close button on notification message and won't close until user click the button
	 * or duration time up, default false.
	 */
	public static void show(String msg, String type, String position, int duration, boolean closable) {
		if (type == null)
			type = NOTIFICATION_TYPE_INFO;
		if (position == null)
			position = "top_center";
		if (!position.matches("^(?:top|middle|bottom)_(?:left|center|right)$"))
			throw new WrongValueException("position: " + position);
		response(new AuToast(msg, type, position, duration, closable, getAnimationSpeed()));
	}

	/**
	 * Display a five-second toast notification message.
	 *
	 * @param msg the message to show (HTML is accepted)
	 * @param type available types are "info", "warning", "error". default "info".
	 * @param position predefined positions. Available options
	 * see {@link #show(String, String, String, int, boolean)}.
	 * default "top_center".
	 */
	public static void show(String msg, String type, String position) {
		show(msg, type, position, 5000, false);
	}

	/**
	 * Display a five-second toast notification message at the top-center of the browser window.
	 *
	 * @param msg the message to show (HTML is accepted)
	 */
	public static void show(String msg) {
		show(msg, null, null, 5000, false);
	}
}
