/* B85_ZK_3828VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 28 09:45:32 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.DependsOn;

/**
 * @author rudyhuang
 */
public class B85_ZK_3828VM {
	private String message = "remove 'error' from this text";

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@DependsOn("message")
	public B85_ZK_3828Status getStatus() {
		return getMessage().contains("error") ? B85_ZK_3828Status.ERROR : B85_ZK_3828Status.OK;
	}
}
