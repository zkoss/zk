/* PerformanceVM.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 12:16:50 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.annotation.Init;

public class PerformanceVM {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Init
	private void init() {
		message = "VM";
	}
}
