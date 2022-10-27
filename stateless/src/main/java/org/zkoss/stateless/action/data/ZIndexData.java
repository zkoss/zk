/* ZIndexData.java

	Purpose:

	Description:

	History:
		5:44 PM 2022/3/2, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.zk.au.AuRequests;

/**
 * Represents an action caused by a component whose z-index is modified
 * by the client.
 * @author jumperchen
 */
public class ZIndexData implements ActionData {
	private final int zIndex;

	@JsonCreator
	private ZIndexData(Map data) {
		this.zIndex = AuRequests.getInt(data, "", -1);
	}

	/** Returns the z-index of the component after moved.
	 */
	public final int getZIndex() {
		return zIndex;
	}
}
