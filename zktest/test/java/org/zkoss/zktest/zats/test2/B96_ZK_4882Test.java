/* B96_ZK_4882Test.java

		Purpose:
		
		Description:
		
		History:
				Fri May 14 15:39:36 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;

public class B96_ZK_4882Test extends ZATSTestCase {
	@Test
	public void indexTest() {
		DesktopAgent desktop = connect();

		String[] testString = {"0A", "1B", "2C", "3D"};
		structureAndIndexCheck(desktop, testString);

		// delete 2nd tab
		desktop.queryAll("tab").get(1).click();
		desktop.queryAll("tabpanel").get(1).queryAll("button").get(1).click();

		String[] testString1 = {"0A", "1C", "2D"};
		structureAndIndexCheck(desktop, testString1);

		// add after 2nd tab
		desktop.queryAll("tab").get(1).click();
		desktop.queryAll("tabpanel").get(1).queryAll("button").get(2).click();

		String[] testString2 = {"0A", "1C", "2C1", "3D"};
		structureAndIndexCheck(desktop, testString2);

		// add before 3rd tab
		desktop.queryAll("tab").get(2).click();
		desktop.queryAll("tabpanel").get(2).queryAll("button").get(3).click();

		String[] testString3 = {"0A", "1C", "2C12", "3C1", "4D"};
		structureAndIndexCheck(desktop, testString3);
	}

	private void structureAndIndexCheck(DesktopAgent desktop, String[] testString) {
		List<ComponentAgent> outerTabs = desktop.queryAll("tab");
		List<ComponentAgent> outerTabpanels = desktop.queryAll("tabpanel");
		Label msg = desktop.query("#msg").as(Label.class);

		int expectedSize = testString.length;
		Assert.assertEquals("shall have " + expectedSize + " tab widgets left", expectedSize, outerTabs.size());
		for (int i = 0; i < expectedSize; i++) {
			ComponentAgent targetTab = outerTabs.get(i);
			targetTab.click();
			ComponentAgent indexbutton = outerTabpanels.get(i).queryAll("button").get(0);
			Assert.assertEquals(testString[i], targetTab.as(Tab.class).getLabel());

			indexbutton.click();
			Assert.assertEquals("item index " + i, msg.getValue());
		}
	}
}
