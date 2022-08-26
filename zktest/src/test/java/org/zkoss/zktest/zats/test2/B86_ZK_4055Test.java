/* B86_ZK_4055Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 13 10:57:34 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B86_ZK_4055Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		Assertions.assertTrue(jq("#div1").exists());
		Assertions.assertTrue(jq("#div2").exists());
		Assertions.assertFalse(jq("#div3").exists());
		Assertions.assertTrue(jq("#div4").exists());
		Assertions.assertFalse(jq("#div5").exists());

		Assertions.assertTrue(jq("#div1 > span").exists(), "Missing ZK label!");
		Assertions.assertTrue(jq("#div2 > span").exists(), "Missing ZK label!");
		Assertions.assertTrue(jq("#div4 > span").exists(), "Missing ZK label!");
	}
}
