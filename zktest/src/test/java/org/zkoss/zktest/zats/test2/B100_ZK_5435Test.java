/* B100_ZK_5435Test.java

	Purpose:
		
	Description:
		
	History:
		5:00 PM 2023/7/25, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
@ForkJVMTestOnly
public class B100_ZK_5435Test extends WebDriverTestCase {

	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B100_ZK_5435Test.class);
	@Test
	public void testDesktopTimeout() {
		connect();
		waitResponse();
		sleep(12000);
		click(jq("@button"));
		waitResponse();
		assertTrue(hasError());
	}
}
