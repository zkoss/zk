/* B102_ZK_5861Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 24 11:31:22 CST 2024, Created by jameschu

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */

public class B102_ZK_5861Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		click(jq(".z-datebox-button"));
		waitResponse();
		click(jq(".z-calendar-cell.z-calendar-weekday").eq(10)); //random date
		waitResponse();
		assertFalse(jq(".z-errorbox").exists());
	}
}
