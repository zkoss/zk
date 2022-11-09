/* B95_ZK_4719Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Nov 16 17:33:38 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.TouchWebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;

public class B95_ZK_4719Test extends TouchWebDriverTestCase {

	@Test
	public void test() {
		connect();
		Element btn = widget("@datebox").$n("btn");
		try {
			tap(toElement(btn));
			waitResponse();
			Assertions.assertTrue(jq(".z-calendar-text").exists(),
					"should show calendar instead of time wheel.");
		} finally {
			click(jq("@button")); // reset library property
			waitResponse();
		}
	}
}
