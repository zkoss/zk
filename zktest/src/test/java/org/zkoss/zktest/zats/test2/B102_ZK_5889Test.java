/* B102_ZK_5889Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 28 11:51:21 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B102_ZK_5889Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktop = connect();
		ComponentAgent cbAgent = desktop.query("#cb");
		cbAgent.type("child1");
		assertEquals(0, cbAgent.as(Combobox.class).getSelectedIndex());
		Label label = desktop.query("#result").as(Label.class);
		String value = label.getValue();
		desktop.query("#saveBtn").click();
		System.out.println(">>>>>>> value: " + value);
		assertNotEquals(value, label.getValue());
		assertFalse(label.getValue().contains("$$"));
	}
}