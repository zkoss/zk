/* B50_ZK_676Test.java

	Purpose:

	Description:

	History:
		11:35 AM 2024/8/28, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B50_ZK_676Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(6, jq(".z-div").length());
		assertEquals("abc\"><script></script><!--", jq(".z-div:eq(3)").attr("title"));
		assertEquals("abc\"><script></script><!--", jq(".z-div:eq(4)").attr("title"));
		assertEquals("abc\"><script></script><!--", jq(".z-div:eq(5)").attr("title"));
	}
}
