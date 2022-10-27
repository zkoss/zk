/* B90_ZK_4482Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Jan 13 18:30:32 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

public class B90_ZK_4482Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertNoJSError();
	}
}
