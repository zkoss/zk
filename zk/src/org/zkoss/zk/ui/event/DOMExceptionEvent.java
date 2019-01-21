/* DOMExceptionEvent.java

		Purpose:
                
		Description:
                
		History:
				Mon Jan 14 15:59:23 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.event;

import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Component;

/**
 * Represents an event caused by DOMException at client.
 * Refers to https://developer.mozilla.org/en-US/docs/Web/API/DOMException
 *
 * @since 8.6.1
 */
public class DOMExceptionEvent extends Event {
	private final String _errorName;
	private final String _errorMessage;

	public DOMExceptionEvent(String name, Component target, String errorName, String errorMessage) {
		super(name, target);
		_errorName = errorName;
		_errorMessage = errorMessage;
	}

	/**
	 * Creates an instance of {@link DOMExceptionEvent} based on the specified request.
	 */
	public static DOMExceptionEvent getDOMExceptionEvent(AuRequest request) {
		Map data = request.getData();
		return new DOMExceptionEvent(request.getCommand(), request.getComponent(),
				(String) data.get("name"), (String) data.get("message"));
	}

	/**
	 * Returns the error name,
	 * to see common error names, please refer to https://developer.mozilla.org/en-US/docs/Web/API/DOMException#Error_names
	 *
	 * @return error name
	 */
	public String getErrorName() {
		return _errorName;
	}

	/**
	 * Returns a message or description associated with the given error name.
	 *
	 * @return error message
	 */
	public String getErrorMessage() {
		return _errorMessage;
	}
}
