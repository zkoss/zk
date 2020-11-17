/* B95_ZK_4628VM.java

		Purpose:
		
		Description:
		
		History:
				Tue Nov 17 16:33:57 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.util.Clients;

public class B95_ZK_4628VM {
	@Command
	public void handleOpenEvent(@ContextParam(ContextType.TRIGGER_EVENT) OpenEvent event) {
		//some event handling
		Clients.log(event.getName());
	}
}
