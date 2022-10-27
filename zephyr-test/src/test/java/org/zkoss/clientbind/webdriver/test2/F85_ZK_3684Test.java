/* F85_ZK_3684Test.java

		Purpose:
                
		Description:
                
		History:
				Fri Mar 29 09:43:56 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

public class F85_ZK_3684Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		verify("", "[FirstName should be capitalized and only letters are accepted., FirstName cannot be empty.]");
		verify("123", "[FirstName should be capitalized and only letters are accepted.]");
		verify("Henry", "[]");
	}

	private void verify(String input, String text) {
		type(jq("@textbox"), input);
		waitResponse();
		Assertions.assertEquals(text, jq("label").text());
	}
}
