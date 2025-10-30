/* BasicPropertyBindingTest.java
	Purpose:

	Description:

	History:
		Fri May 07 14:24:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.propertybinding;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class TemplatePropertyBindingTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		//[Step 1]
		Grid g1 = desktopAgent.query("#g1").as(Grid.class);
		Grid g2 = desktopAgent.query("#g2").as(Grid.class);
		List<Component> g1Rows = g1.getRows().getChildren();
		List<Component> g2Rows = g2.getRows().getChildren();
		assertEquals(g1Rows.size(), g2Rows.size());
		for (int i = 0; i < g1Rows.size(); i++) {
			List<Label> r1Labels = g1Rows.get(i).getChildren();
			List<Label> r2Labels = g2Rows.get(i).getChildren();
			for (int j = 0; j < r1Labels.size(); j++) {
				assertEquals(r1Labels.get(j).getValue(), r2Labels.get(j).getValue());
			}
		}
		//[Step 2]
		Grid g3 = desktopAgent.query("#g3").as(Grid.class);
		List<Component> g3Rows = g3.getRows().getChildren();
		for (int i = 0; i < g3Rows.size(); i++) {
			Label idLabel = (Label) g3Rows.get(i).getChildren().get(1);
			assertEquals("*****", idLabel.getValue());
		}
		//[Step 3]
		desktopAgent.query("#btn1").click();
		for (int i = 0; i < g3Rows.size(); i++) {
			Label idLabel = (Label) g3Rows.get(i).getChildren().get(1);
			assertNotEquals("*****", idLabel.getValue());
		}
		desktopAgent.query("#btn1").click();
		for (int i = 0; i < g3Rows.size(); i++) {
			Label idLabel = (Label) g3Rows.get(i).getChildren().get(1);
			assertEquals("*****", idLabel.getValue());
		}
		//[Step 4]
		Grid g4 = desktopAgent.query("#g4").as(Grid.class);
		List<Component> g4Rows = g4.getRows().getChildren();
		desktopAgent.query("#btn2_1").click();
		assertEquals("v", ((Label) g4Rows.get(0).getChildren().get(4)).getValue());
		//[Step 5]
		desktopAgent.query("#btn2_2").click();
		assertEquals("", ((Label) g4Rows.get(0).getChildren().get(4)).getValue());
	}
}
