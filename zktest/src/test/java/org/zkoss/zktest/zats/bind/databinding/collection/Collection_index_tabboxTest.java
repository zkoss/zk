/* Collection_index_tabboxTest.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 28 11:36:56 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class Collection_index_tabboxTest extends WebDriverTestCase {
	@Test
	public void indexTest() {
		connect("/bind/databinding/collection/collection-index-tabbox.zul");

		JQuery outerTabs = jq("$outerbox>.z-tabs:eq(0) .z-tab");
		JQuery outerTabpanels = jq("$outerbox>.z-tabpanels:eq(0)>.z-tabpanel");
		String[] testString = {"0A", "1B", "2C", "3D"};
		structureAndIndexCheck(outerTabs, outerTabpanels, testString);

		// delete 2nd tab
		click(outerTabs.eq(1));
		waitResponse();
		click(outerTabpanels.eq(1).find("@button:contains(Delete)"));
		waitResponse();

		String[] testString1 = {"0A", "2C", "3D"};
		structureAndIndexCheck(outerTabs, outerTabpanels, testString1);

		// add after 2nd tab
		click(outerTabs.eq(1));
		waitResponse();
		click(outerTabpanels.eq(1).find("@button:contains(Add After)"));
		waitResponse();

		String[] testString2 = {"0A", "2C", "2C1", "3D"};
		structureAndIndexCheck(outerTabs, outerTabpanels, testString2);

		// add before 3rd tab
		click(outerTabs.eq(2));
		waitResponse();
		click(outerTabpanels.eq(2).find("@button:contains(Add Before)"));
		waitResponse();

		String[] testString3 = {"0A", "2C", "2C12", "2C1", "3D"};
		structureAndIndexCheck(outerTabs, outerTabpanels, testString3);
	}

	private void structureAndIndexCheck(JQuery outerTabs, JQuery outerTabpanels, String[] testString) {
		int expectedSize = testString.length;
		Assertions.assertEquals(expectedSize, outerTabs.length(), "shall have " + expectedSize + " tab widgets left");
		JQuery msg = jq("$msg");
		for (int i = 0; i < expectedSize; i++) {
			JQuery targetTab = outerTabs.eq(i);
			click(targetTab);
			waitResponse();
			JQuery targetTabpanel = outerTabpanels.eq(i);
			Assertions.assertEquals(testString[i], targetTab.text());
			Assertions.assertEquals(2, targetTabpanel.find(".z-tab").length(), "shall see 2 tab in the inner tabbox");

			click(targetTabpanel.find("@button:contains(Index)"));
			waitResponse();
			// TODO: wait for ZK-4882 fixed
			// Assertions.assertEquals("item index " + i, msg.text());
		}
	}
}
