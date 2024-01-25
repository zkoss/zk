/* B100_ZK_5535Test.java

	Purpose:
		
	Description:
		
	History:
		12:4PM 2023/12/1, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B100_ZK_5535Test extends WebDriverTestCase {

	@Test
	public void testPerformance() {
		connect();
		click(jq("$show"));
		sleep(30_000);
		waitResponse();
		click(jq("$clear"));
		sleep(5_000);
		assertFalse(jq(".z-loading-indicator").exists());
	}
}
