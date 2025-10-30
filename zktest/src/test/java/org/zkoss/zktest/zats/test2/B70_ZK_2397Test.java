/* B70_ZK_2397Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 01 16:27:46 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@ForkJVMTestOnly
public class B70_ZK_2397Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B70_ZK_2397Test.class);

	@Test
	public void test() {
		connect();
		waitResponse();
		sleep(1000);

		click(jq("@textbox"));
		waitResponse();

		assertTrue(hasError());
		assertEquals("Session timeout. Please reload.", getMessageBoxContent());

		click(jq("@button:contains(Reload)"));
		sleep(100);
		assertFalse(hasError());
	}
}
