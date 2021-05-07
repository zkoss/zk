/* HeaderParamVM.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 15:03:24 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.HeaderParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class HeaderParamVM {
	String headerParam;
	String btnLabel;

	@Init
	public void init(@HeaderParam("host") String host) {
		headerParam = host;
		btnLabel = "test";
	}

	@NotifyChange("btnLabel")
	@Command
	public void cmd(@HeaderParam("host") String host) {
		btnLabel = host;
	}

	public String getHeaderParam() {
		return headerParam;
	}

	public String getBtnLabel() {
		return btnLabel;
	}
}
