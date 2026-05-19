/* F110_ZK_6096VM.java

        Purpose:
                
        Description:
                
        History:
                Tue Oct 20 12:39:27 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.StubComponent;

public class F110_ZK_6096VM {
	private String status = "old";
	private String stubState = "";

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view,
			@ContextParam(ContextType.BINDER) Binder binder) {
		final Component programmatic = view.query("#g11programmatic");
		if (programmatic != null) {
			binder.addPropertyLoadBindings(programmatic, "value", "vm.status", null, null, null, null, null);
			binder.loadComponent(programmatic, true);
		}
	}

	public String getStatus() {
		return status;
	}

	@NotifyChange("status")
	public void setStatus(String status) {
		this.status = status;
	}

	public String getStubState() {
		return stubState;
	}

	@Command
	@NotifyChange("status")
	public void changeStatus() {
		status = "new";
	}

	@Command
	@NotifyChange("stubState")
	public void probeStubs(@ContextParam(ContextType.VIEW) Component view) {
		final StringBuilder sb = new StringBuilder();
		appendStubIds(view, sb);
		stubState = sb.toString().trim();
	}

	private static void appendStubIds(Component comp, StringBuilder sb) {
		if (comp instanceof StubComponent)
			sb.append(comp.getId()).append(' ');
		for (Component child = comp.getFirstChild(); child != null; child = child.getNextSibling())
			appendStubIds(child, sb);
	}
}
