/* B80_ZK_1987Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 15 16:13:47 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.impl.ClientCtrl;
import org.zkoss.zats.mimic.impl.EventDataManager;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B80_ZK_1987Test extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();

		final Label result = desktop.query("window #result").as(Label.class);
		final ComponentAgent comboAgent = desktop.query("combobox");
		final Combobox combobox = comboAgent.as(Combobox.class);
		Assert.assertEquals(1, combobox.getSelectedIndex());

		// should fire onChange to trigger save
		fireOnChange(comboAgent, "bar", "foo");
		comboAgent.getChild(2).select();
		Assert.assertEquals("Element(id=3, label=bar)", result.getValue());
		fireOnChange(comboAgent, "foo", "bar");
		comboAgent.getChild(1).select();
		Assert.assertEquals("Element(id=2, label=foo)", result.getValue());
		comboAgent.getChild(0).select();
		Assert.assertEquals("Element(id=1, label=foo)", result.getValue());
		comboAgent.getChild(1).select();
		Assert.assertEquals("Element(id=2, label=foo)", result.getValue());
	}

	private void fireOnChange(ComponentAgent target, String newVal, String oldVal) {
		String desktopId = (target).getDesktop().getId();
		Event event = new InputEvent(Events.ON_CHANGE, (Component) target.getDelegatee(), newVal, oldVal);
		Map<String, Object> data = EventDataManager.getInstance().build(event);

		final ClientCtrl client = (ClientCtrl) target.getClient();
		client.postUpdate(desktopId, target.getUuid(), event.getName(), data, false);
		client.flush(desktopId);
	}
}
