/* RequestDateFactory.java

	Purpose:

	Description:

	History:
		Fri Dec 10 10:45:01 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import java.util.Map;

/**
 * The factory to provide {@link RequestData} instance.
 * @author katherine
 */
public class RequestDataFactory {
	public static RequestData newInstance(Map data) {
		return new RequestData(data);
	}
}
