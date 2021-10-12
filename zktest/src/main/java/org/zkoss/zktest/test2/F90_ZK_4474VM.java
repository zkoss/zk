/* F90_ZK_4474VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Feb 6 12:26:36 CST 2019, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.NotifyCommand;
import org.zkoss.bind.annotation.ToClientCommand;
import org.zkoss.bind.annotation.ToServerCommand;

/**
 * @author jameschu
 */
@NotifyCommand(value = "toClient", onChange = "_vm_.message")
@ToClientCommand("toClient")
@ToServerCommand("toServer")
public class F90_ZK_4474VM {
	private String message = "Hello";
	private int count = 0;

	public String getMessage() {
		return message + count;
	}

	@Command
	@NotifyChange("message")
	public void toServer() {
		count++;
	}
}
