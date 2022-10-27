/* B95_ZK_4688Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 5 11:18:11 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B95_ZK_4688Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery btns = jq("@button");
		click(btns.eq(0));
		waitResponse();
		JQuery resultLabel = jq("$result");
		Assertions.assertTrue(resultLabel.text().endsWith("1,null"));
		click(btns.eq(1));
		waitResponse();
		Assertions.assertTrue(resultLabel.text().equals("0,null"));
	}
}
