/* DOMExceptionData.java

	Purpose:

	Description:

	History:
		2:57 PM 2022/3/22, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

/**
 * Represents an action caused by DOMException at client.
 * Refers to https://developer.mozilla.org/en-US/docs/Web/API/DOMException
 *
 * @author jumperchen
 */
public class DOMExceptionData implements ActionData {
	private String name;
	private String message;
	/**
	 * Returns the error name,
	 * to see common error names, please refer to https://developer.mozilla.org/en-US/docs/Web/API/DOMException#Error_names
	 */
	public String getErrorName() {
		return name;
	}

	/**
	 * Returns a message or description associated with the given error name.
	 */
	public String getErrorMessage() {
		return message;
	}
}
