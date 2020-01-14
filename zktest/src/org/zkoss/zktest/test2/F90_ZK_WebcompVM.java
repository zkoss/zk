/* F90_ZK_WebcompVM.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 21 10:51:48 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Notification;

/**
 * @author rudyhuang
 */
public class F90_ZK_WebcompVM {
	@Command
	public void now() {
		Clients.showNotification(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
	}

	@Command
	public void handle(@ContextParam(ContextType.TRIGGER_EVENT) F90_ZK_WC_PasteEvent event) {
		Notification.show("You paste: " + event.getClipboardText());
	}
}
