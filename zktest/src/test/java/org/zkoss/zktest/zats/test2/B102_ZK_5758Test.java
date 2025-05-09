/* B102_ZK_5758Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 23 17:00:55 CST 2025, Created by jameschu

Copyright (C) 2024 2025 Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */

public class B102_ZK_5758Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktopAgent = connect();
		List<ComponentAgent> buttons = desktopAgent.queryAll("button");
		Label label1 = buttons.get(0).getNextSibling().as(Label.class);
		Label label2 = buttons.get(1).getNextSibling().as(Label.class);
		Label label3 = buttons.get(2).getNextSibling().as(Label.class);

		String label1Text = label1.getValue();
		String label2Text = label2.getValue();
		String label3Text = label3.getValue();

		buttons.get(0).click();
		assertNotEquals(label1Text, label1.getValue());
		label1Text = label1.getValue();
		assertEquals(label2Text, label2.getValue());
		assertEquals(label3Text, label3.getValue());
		buttons.get(1).click();
		assertEquals(label1Text, label1.getValue());
		assertNotEquals(label2Text, label2.getValue());
		label2Text = label2.getValue();
		assertEquals(label3Text, label3.getValue());
		buttons.get(2).click();
		assertEquals(label1Text, label1.getValue());
		assertEquals(label2Text, label2.getValue());
		assertNotEquals(label3Text, label3.getValue());
	}
}
