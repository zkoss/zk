/* F90_ZK_4472VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 26 12:26:36 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.ToServerCommand;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
@ToServerCommand("toServer")
public class F90_ZK_4472VM {
	@Command
	public void toServer(@ContextParam(ContextType.TRIGGER_EVENT) UploadEvent uploadEvent) {
		Clients.log(uploadEvent != null ? uploadEvent.getMedia().getFormat() : "null");
	}
}
