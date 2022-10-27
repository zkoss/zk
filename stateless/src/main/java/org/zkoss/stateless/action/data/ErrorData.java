/* ErrorData.java

	Purpose:

	Description:

	History:
		2:42 PM 2022/2/15, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

/**
 * Represents an action cause by user's entering a wrong data
 * or clearing the last wrong data.
 * @author jumperchen
 */
public class ErrorData extends InputData {
	private String message;

	/** Returns the error message if this event is caused by a wrong data,
	 * or null if it is to clear message.
	 */
	public final String getMessage() {
		return message;
	}

}
