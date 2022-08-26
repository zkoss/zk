/* B95_ZK_4585Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Aug 31 18:12:55 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B95_ZK_4585Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		int defaultTop = jq(".z-checkbox-content").eq(0).positionTop();
		Assertions.assertEquals(defaultTop, jq(".z-checkbox-content").eq(1).positionTop(), 1, "label of tristate mold is misaligned");
		Assertions.assertEquals(defaultTop, jq(".z-checkbox-content").eq(2).positionTop(), 1, "label of switch mold is misaligned");
		Assertions.assertEquals(defaultTop, jq(".z-checkbox-content").eq(3).positionTop(), 1, "label of toggle mold is misaligned");
	}
}
