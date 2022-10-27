/* IPanelTest.java

	Purpose:

	Description:

	History:
		Thu Oct 21 14:51:16 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;

/**
 * Test for {@link IPanel}
 *
 * @author katherine
 */
public class IPanelTest extends StatelessTestBase {
	@Test
	public void withPanel() {
		// check Richlet API case
		assertEquals(richlet(() -> IPanel.of(IPanelchildren.of(ILabel.of("abc"))).withVflex("1")), zul(IPanelTest::newPanel));

		// check Stateless file case
		assertEquals(composer(IPanelTest::newPanel), zul(IPanelTest::newPanel));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Panel panel = new Panel();
					panel.setVflex("1");
					Panelchildren panelChildren = new Panelchildren();
					panelChildren.appendChild(new Label("123"));
					panel.appendChild(panelChildren);
					return panel;
				}, (IPanel iPanel) -> iPanel.withPanelchildren(IPanelchildren.of(ILabel.of("abc"))).withVflex("1")),
				zul(IPanelTest::newPanel));
	}

	private static Panel newPanel() {
		Panel panel = new Panel();
		panel.setVflex("1");
		Panelchildren panelChildren = new Panelchildren();
		panelChildren.appendChild(new Label("abc"));
		panel.appendChild(panelChildren);
		return panel;
	}
}