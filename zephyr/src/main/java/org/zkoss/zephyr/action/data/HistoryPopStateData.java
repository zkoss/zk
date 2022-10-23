/* HistoryPopStateData.java

	Purpose:
		
	Description:
		
	History:
		4:11 PM 2022/8/12, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

/** The history pop state action used with <code>onHistoryPopState</code>
 * to notify that user pressed BACK, FORWARD or others
 * that causes the history changed (but still in the same desktop).
 *
 * <p>All root components of all pages of the desktop will
 * receives this action.
 * @author jumperchen
 */
public class HistoryPopStateData implements ActionData {

	private Object state;
	private String url;

	/**
	 * Returns the state object.
	 * @return the state object.
	 */
	public Object getState() {
		return state;
	}

	/**
	 * Returns the URL.
	 * @return the URL.
	 */
	public String getUrl() {
		return url;
	}
}
