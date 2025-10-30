/* Collection_template_dependenceTest.java

		Purpose:
		
		Description:
		
		History:
				Wed Apr 28 15:43:00 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class Collection_template_dependenceTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/bind/databinding/collection/collection-template-dependence.zul");

		String[] expectedItemName = {"A", "B", "C", "D", "E"};
		String[] expectedTemplateName = {"Model1", "Model2", "Model1", "Model2", "Model2"};
		checkTemplateDisplay(expectedItemName, expectedTemplateName);


		click(jq("@button:contains(change 1)"));
		waitResponse();
		String[] expectedItemName2 = {"X", "A", "C", "D", "E"};
		String[] expectedTemplateName2 = {"Model2", "Model1", "Model1", "Model2", "Model2"};
		checkTemplateDisplay(expectedItemName2, expectedTemplateName2);

		click(jq("@button:contains(change 2)"));
		waitResponse();
		String[] expectedItemName3 = {"A", "X", "C", "D", "E"};
		String[] expectedTemplateName3 = {"Model1", "Model2", "Model1", "Model2", "Model2"};
		checkTemplateDisplay(expectedItemName3, expectedTemplateName3);
	}

	private void checkTemplateDisplay(String[] expectedItemName, String[] expectedTemplateName) {
		JQuery rows = jq(".z-row");
		for (int i = 0; i < 5; i++) {
			Assertions.assertEquals(expectedItemName[i], rows.eq(i).find(".z-row-content:eq(0)").text());
			Assertions.assertEquals(expectedTemplateName[i], rows.eq(i).find(".z-row-content:eq(1)").text());
		}
	}
}
