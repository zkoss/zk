/* B95_ZK_4685Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
@Disabled
public class B95_ZK_4685Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		JQuery button = jq("@button");
		click(button.eq(0));
		waitResponse();
		click(jq("$closeBtn"));
		waitResponse();
		click(button.eq(1));
		waitResponse();
		click(jq("$closeBtn"));
		waitResponse();
		assertEquals("[formContent.commonProp, formContent.prop1][formContent.commonProp, formContent.prop2]", jq("$result").text());
	}
}
