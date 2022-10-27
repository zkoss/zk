/* SlideData.java

	Purpose:

	Description:

	History:
		4:19 PM 2022/3/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

/** Represents an action cause by user's slidedown or slideup
 * something at the client.
 *
 * <p>Note: When using with LayoutRegion component, we couldn't guarantee the triggered
 * order of {@code onClick} action and {@code onSlide} action when clicking out
 * of the region but on elements that is listened to the {@code onClick} action.
 *
 * @author jumperchen
 */
public class SlideData implements ActionData {
	private boolean slide;

	/** Returns whether it is slide out.
	 */
	public boolean isSlide() {
		return slide;
	}
}
