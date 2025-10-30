/* B50_ZK_724Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 23 18:09:35 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B50_ZK_724Test extends WebDriverTestCase {
	@Test
	public void test() {
		try {
			connect();
			click(jq("@button"));
			waitResponse();
		} catch (Exception e) {
			Assertions.fail();
		}
	}
}
