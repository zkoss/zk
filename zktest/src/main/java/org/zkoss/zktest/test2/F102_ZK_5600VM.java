/* F102_ZK_5600VM.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 15 11:37:39 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.event.KeyEvent;

public class F102_ZK_5600VM {
	@Command
	public void cmd1(@ContextParam(ContextType.TRIGGER_EVENT) KeyEvent event) {
		System.out.println(event);
	}

	@Command
	public void cmd2(@BindingParam("event") KeyEvent event) {
		System.out.println(event);
	}
}
