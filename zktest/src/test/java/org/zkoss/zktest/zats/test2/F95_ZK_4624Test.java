/* F95_ZK_4624Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 21 12:03:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class F95_ZK_4624Test extends WebDriverTestCase {
	@Test
	public void testScroll() {
		connect("/test2/F95-ZK-4624-scroll.zul");
		waitResponse();
		Assertions.assertTrue(jq("$l2").positionTop() >= 0);

		click(jq("$sToTopBtn"));
		waitResponse();
		Assertions.assertEquals(0, jq("$vm").scrollTop(), 10);

		click(jq("$sToList"));
		waitResponse();
		JQuery list = jq("$list").eq(0);
		JQuery last = list.children("div").last();
		Assertions.assertTrue(last.positionTop() > list.height());
	}

	@Test
	public void testFocus() {
		connect("/test2/F95-ZK-4624-focus.zul");
		waitResponse();
		Assertions.assertTrue(jq("$tb").is(":focus"));

		click(jq("$fcIntoListLastBtn"));
		waitResponse();
		Assertions.assertTrue(jq("@button").last().is(":focus"));

		click(jq("$fcIntoInnerTb"));
		waitResponse();
		Assertions.assertTrue(jq("$win3 $tb_inner").is(":focus"));
	}
}
