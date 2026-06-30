/* F104_ZK_6096Composer.java

		Purpose:

		Description:

		History:
				Tue May 19 16:10:10 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.sys.StubComponent;
import org.zkoss.zul.Label;

public class F104_ZK_6096Composer extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	private static final String[] PROBE_IDS = {
			"g0lbl", "g0btn",
			"g1", "g1div", "g1lbl", "g1btn",
			"g2", "g2divLive", "g2divStub", "g2lbl", "g2lblStub",
			"g3", "g3div", "g3lbl", "g3btn",
			"g5", "g5myDiv", "g5lbl",
			"g6",
			"probe"
	};

	@Wire("#result")
	private Label result;

	@Listen("onClick = #probe")
	public void probe(Event ignored) {
		final StringBuilder sb = new StringBuilder();
		for (String id : PROBE_IDS) {
			final Component c = findById(getSelf(), id);
			if (sb.length() > 0)
				sb.append(',');
			sb.append(id).append('=');
			if (c == null)
				sb.append("MISSING");
			else if (c instanceof StubComponent)
				sb.append("STUB");
			else
				sb.append("LIVE");
		}
		result.setValue(sb.toString());
	}

	private static Component findById(Component root, String id) {
		if (id.equals(root.getId()))
			return root;
		for (Component child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
			final Component found = findById(child, id);
			if (found != null)
				return found;
		}
		return null;
	}
}
