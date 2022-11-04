/* B85_ZK_3585Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 13 19:04:30 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3585Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		JQuery city = jq("@textbox").eq(2);
		String cityValue = city.val();
		click(jq("@button").eq(0));
		waitResponse();
		Assertions.assertNotEquals(cityValue, city.val());

		JQuery collectionHbox = jq("@hbox").eq(5);
		int numOfCollectionLabel = collectionHbox.find("@vlayout").children().length();
		click(jq("@button").eq(3));
		waitResponse();
		Assertions.assertEquals(numOfCollectionLabel + 1, collectionHbox.find("@vlayout").children().length());

		JQuery mapHbox = jq("@hbox").eq(6);
		int numOfMapDiv = mapHbox.find("@vlayout").children().length();
		click(jq("button").eq(4));
		waitResponse();
		Assertions.assertEquals(numOfMapDiv + 1, mapHbox.find("@vlayout").children().length());

		JQuery childName = jq("@textbox").eq(3);
		String name = childName.val();
		click(jq("button").eq(5));
		waitResponse();
		Assertions.assertNotEquals(name, childName.val());
	}
}
