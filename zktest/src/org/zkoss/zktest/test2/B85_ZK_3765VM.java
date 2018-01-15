/* B85_ZK_3765VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 15 15:03:54 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;

/**
 * @author rudyhuang
 */
public class B85_ZK_3765VM {
	@Wire
	private Div div1;

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component component) {
		Selectors.wireComponents(component, this, false);

		Executions.createComponents("B85-ZK-3765-include.zul", div1, null);
	}

	public boolean getVisible1() {
		return true;
	}

	public boolean getVisible2() {
		return true;
	}

	@Command
	public void onClick1() {
		Messagebox.show("1- " + WebApps.getCurrent().getVersion());
	}

	@Command
	public void onClick2() {
		Messagebox.show("2- " + WebApps.getCurrent().getVersion());
	}
}
