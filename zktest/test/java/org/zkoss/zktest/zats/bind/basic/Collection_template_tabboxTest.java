/* Collection_template_tabboxTest.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 28 16:21:35 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class Collection_template_tabboxTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/basic/collection-template-tabbox.zul");

		JQuery outerTabs = jq("$outerbox>.z-tabs:eq(0) .z-tab");

		String[] expectedTabName = {"0AModel1", "1BModel2", "2CModel1", "3DModel2"};
		structureAndIndexCheck(outerTabs, expectedTabName);

		click(jq("@button:contains(change1)"));
		waitResponse();
		String[] expectedTabName2 = {"0XModel2", "1AModel1", "2CModel1", "3DModel2"};
		structureAndIndexCheck(outerTabs, expectedTabName2);

		click(jq("@button:contains(change2)"));
		waitResponse();
		String[] expectedTabName3 = {"0AModel2", "1BModel1", "2CModel1", "3DModel2"};
		structureAndIndexCheck(outerTabs, expectedTabName3);
	}

	private void structureAndIndexCheck(JQuery outerTabs, String[] testString) {
		int expectedSize = testString.length;
		for (int i = 0; i < expectedSize; i++) {
			JQuery targetTab = outerTabs.eq(i);
			Assert.assertEquals(testString[i], targetTab.text());
		}
	}
}
