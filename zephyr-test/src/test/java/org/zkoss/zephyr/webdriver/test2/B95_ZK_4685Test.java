/* B95_ZK_4685Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4685Test extends ClientBindTestCase {
	@Test
	public void test() throws Exception {
		connect();
		//TODO: refine
//		JQuery button = jq("@button");
//		click(button.eq(0));
//		waitResponse();
//		click(jq("$closeBtn"));
//		waitResponse();
//		click(button.eq(1));
//		waitResponse();
//		click(jq("$closeBtn"));
//		waitResponse();
//		Assertions.assertEquals("[formContent.commonProp, formContent.prop1][formContent.commonProp, formContent.prop2]", jq("$result").text());
	}
}
