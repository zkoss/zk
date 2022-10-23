/* CheckData.java

	Purpose:

	Description:

	History:
		2:59 PM 2022/2/22, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.zk.au.AuRequests;

/**
 * Represents an action cause by user's check a state at the client.
 *
 * @author jumperchen
 */
public class CheckData implements ActionData {
	private final Boolean checked;

	@JsonCreator
	private CheckData(Map data) {
		checked = AuRequests.getBoolean(data, "");
	}

	/** Returns whether the state is checked.
	 */
	public final boolean isChecked() {
		return (checked == null) ? false : checked;
	}

	/** Returns the nullable state is changed.
	 */
	@Nullable
	public final Boolean getState() {
		return checked;
	}

	/**
	 * @hidden for Javadoc
	 */
	public String toString() {
		return "CheckData{" + "checked=" + checked + '}';
	}
}
