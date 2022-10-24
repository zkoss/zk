/* B95_ZK_4655VM.java

	Purpose:
		
	Description:
		
	History:
		Fri Nov 20 12:14:49 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.ToServerCommand;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.sys.Storage;

/**
 * @author rudyhuang
 */
@ToServerCommand("update")
public class B95_ZK_4655VM {
	private Integer count;

	public Integer getCount() {
		return count;
	}

	@Init
	public void init(@ContextParam(ContextType.DESKTOP) Desktop desktop) {
		count = 100;
		syncToStorage(desktop);
	}

	private void syncToStorage(Desktop desktop) {
		Storage desktopStorage = desktop.getStorage();
		desktopStorage.setItem("count", count);
	}

	@Command
	@NotifyChange("count")
	public void cmd(@ContextParam(ContextType.DESKTOP) Desktop desktop) {
		count++;
		syncToStorage(desktop);
	}

	@Command("update")
	@NotifyChange("count")
	public void doUpdate(@ContextParam(ContextType.DESKTOP) Desktop desktop) {
		count = desktop.getStorage().getItem("count");
	}
}
