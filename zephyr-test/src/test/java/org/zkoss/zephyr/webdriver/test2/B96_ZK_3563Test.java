/* B96_ZK_3563Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 04 12:46:50 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B96_ZK_3563Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery saveBtn = jq("$saveBtn");
		click(saveBtn);
		waitResponse();
		click(saveBtn);
		waitResponse();
		click(saveBtn);
		waitResponse();
		JQuery label2 = jq("$l2");
		Assertions.assertEquals(jq("$l1").text(), label2.text());
		click(jq("$serializeBtn"));
		waitResponse();
		click(saveBtn);
		waitResponse();
		click(saveBtn);
		waitResponse();
		click(saveBtn);
		waitResponse();
		Assertions.assertEquals(jq("$l1").text(), label2.text());
	}
}
