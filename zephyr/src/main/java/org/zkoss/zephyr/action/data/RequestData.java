/* RequestData.java

	Purpose:

	Description:

	History:
		2:11 PM 2021/10/21, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Represents request data from an action
 * @author jumperchen
 */
public class RequestData implements ActionData {
	private Map data;

	@JsonCreator
	protected RequestData(Map data) {
		this.data = data;
	}

	/**
	 * Returns the request data if any.
	 */
	public Map getData() {
		return data;
	}

	/**
	 * @hidden for Javadoc
	 */
	public String toString() {
		return "RequestData{" + "data=" + data + '}';
	}
}
