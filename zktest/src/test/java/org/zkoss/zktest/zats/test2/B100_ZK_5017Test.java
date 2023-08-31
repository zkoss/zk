/* B100_ZK_5017Test.java

	Purpose:
		
	Description:
		
	History:
		3:28 PM 2023/7/26, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.RepeatedTest;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B100_ZK_5017Test extends WebDriverTestCase {
	@RepeatedTest(3)
	public void test() {
		connect();
		waitResponse();
		assertEquals(jq(".z-listbox-header").css("display"), "none");
	}

	protected void waitResponse() {
		while(Boolean.valueOf(this.getEval("!!zAu.processing()"))) {
			sleep(10);
		}
		sleep(10);
	}
}
