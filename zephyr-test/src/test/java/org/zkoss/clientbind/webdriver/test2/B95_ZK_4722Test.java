/* B95_ZK_4685Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 10:25:45 AM CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
@Disabled
public class B95_ZK_4722Test extends ClientBindTestCase {
	@Test
	public void test() throws Exception {
		connect();
		sleep(2000);
		JQuery saveBtn = jq("$save");
		click(saveBtn);
		waitResponse();
		Assertions.assertEquals("4", jq("$result").text());
		click(saveBtn);
		waitResponse();
		Assertions.assertEquals("4", jq("$result").text());
		click(saveBtn);
		waitResponse();
		Assertions.assertEquals("4", jq("$result").text());
		click(jq("$detach"));
		waitResponse();
		click(saveBtn);
		waitResponse();
		Assertions.assertEquals("1", jq("$result").text());
	}
}
