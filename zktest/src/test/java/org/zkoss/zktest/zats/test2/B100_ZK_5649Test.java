/* B100_ZK_5649Test.java

	Purpose:
		
	Description:
		
	History:
		11:44 AM 2024/2/19, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B100_ZK_5649Test extends WebDriverTestCase {
	@Test
	public void testCase1() {
		connect();
		waitResponse();
		String mvvmCase = jq(".mvvm-case .z-label").text();
		String elCase = jq(".el-case .z-label").text();
		sleep(1500);
		click(jq("@button"));
		waitResponse();
		assertNotEquals(mvvmCase, jq(".mvvm-case .z-label").text());
		assertNotEquals(elCase, jq(".el-case .z-label").text());
	}

	@Test
	public void testCase2() {
		connect("/test2/B100-ZK-5649-2.zul");
		waitResponse();
		String elCase = jq(".el-case .z-label").text();
		sleep(1500);
		click(jq("@button"));
		waitResponse();
		assertNotEquals(elCase, jq(".el-case .z-label").text());
	}
}
