/* F110_ZK_6096Composer.java

		Purpose:

		Description:

		History:
				Tue May 19 16:10:10 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.sys.StubComponent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;

public class F110_ZK_6096Composer extends SelectorComposer<Component> {
	@Wire
	private Label result;
	@Wire
	private Div g4live;
	@Wire
	private Hlayout g5live;

	@Listen("onClick = #append")
	public void append() {
		final Label label = new Label("g4-new");
		label.setId("g4newLbl");
		g4live.appendChild(label);
		final Div div = new Div();
		div.setId("g4newDiv");
		g4live.appendChild(div);
		final Label label5 = new Label("g5-new");
		label5.setId("g5newLbl");
		g5live.appendChild(label5);
	}

	@Listen("onClick = #probe")
	public void probe() {
		final StringBuilder sb = new StringBuilder();
		appendStubIds(getSelf(), sb);
		result.setValue(sb.toString().trim());
	}

	private static void appendStubIds(Component comp, StringBuilder sb) {
		if (comp instanceof StubComponent)
			sb.append(comp.getId()).append(' ');
		for (Component child = comp.getFirstChild(); child != null; child = child.getNextSibling())
			appendStubIds(child, sb);
	}
}
