/* C2Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 26 11:52:03 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author rudyhuang
 */
public class C2Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 4; i >= 0; i--) {
			click(jq("@button"));
			waitResponse();
			assertEquals(i, jq("@listitem").length());
		}
	}
}
