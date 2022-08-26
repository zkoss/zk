/* B70_ZK_2518Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 03 11:16:29 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2518Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertEquals(".png,.pdf", jq(".z-upload input[type=\"file\"]").attr("accept"));
	}
}
