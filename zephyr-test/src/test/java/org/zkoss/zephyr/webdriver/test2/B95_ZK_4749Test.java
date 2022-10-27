/* B95_ZK_4749Test.java

	Purpose:

	Description:

	History:
		Fri Dec 18 10:50:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4749Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$prefillBtn"));
		waitResponse();
		Assertions.assertNotEquals("", jq("$l1").text().trim());
		Assertions.assertNotEquals("", jq("$l2").text().trim());
		Assertions.assertNotEquals("", jq("$l2_1").text().trim());
		Assertions.assertNotEquals("", jq("$l3").text().trim());
	}
}
