/* MarshalledResponse.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 16 22:14:48 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.au;

import java.util.List;

/**
 * Marshalled AU response ({@link AuResponse}).
 * @author tomyeh
 * @since 5.0.4
 */
public class MarshalledResponse implements java.io.Serializable {
	/** The command. */
	public final String command;
	/** The encoded data ({@link AuResponse}). */
	public final List encodedData;

	public MarshalledResponse(AuResponse response) {
		this.command = response.getCommand();
		this.encodedData = response.getEncodedData();
	}
}
