/* RequestOutOfSequenceException.java

	Purpose:
		
	Description:
		
	History:
		Sat Jul 24 13:59:15 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.au;

import org.zkoss.zk.ui.UiException;

/**
 * Indicates a request is out-of-sequence, and the request shall be ignored.
 *
 * @author tomyeh
 * @since 5.0.4
 */
public class RequestOutOfSequenceException extends UiException {
	private final String _reqId;
	private final String _oldReqId;
	public RequestOutOfSequenceException(String reqId, String oldReqId) {
		super("Request "+reqId+" out-of-sequence when processing "+oldReqId);
		_reqId = reqId;
		_oldReqId = oldReqId;
	}
	/** Returns the sequence ID of the new request.
	 */
	public String getRequestId() {
		return _reqId;
	}
	/** Returns the sequence ID of the request being processed.
	 */
	public String getProcessingId() {
		return _oldReqId;
	}
}
