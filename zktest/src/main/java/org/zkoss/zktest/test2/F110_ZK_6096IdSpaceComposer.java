/* F110_ZK_6096IdSpaceComposer.java

        Purpose:
                
        Description:
                
        History:
                Tue Oct 20 12:39:49 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.sys.StubComponent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class F110_ZK_6096IdSpaceComposer extends SelectorComposer<Component> {
	@Listen("onClick = #spaceButton")
	public void checkIdSpace(Event event) {
		final Component target = event.getTarget();
		final Label result = (Label) target.getFellow("spaceResult");
		final Component owner = (Component) target.getSpaceOwner();
		boolean staticStub = false;
		for (Component child = owner.getFirstChild(); child != null; child = child.getNextSibling()) {
			if ("spaceStatic".equals(child.getId()))
				staticStub = child instanceof StubComponent;
		}
		final String type = owner instanceof Window ? "Window"
				: owner instanceof HtmlMacroComponent && !((HtmlMacroComponent) owner).isInline()
						? "Non-inline macro" : "Other";
		result.setValue(type + "; same owner: " + (owner == getSelf()) + "; static stub: " + staticStub);
	}
}
