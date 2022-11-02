/* B85_ZK_3949Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 14 14:51:46 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

public class B85_ZK_3949Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$hidebtn"));
		waitResponse();
		Assertions.assertFalse(jq("@caption").toWidget().$n("img").exists());
	}
}
