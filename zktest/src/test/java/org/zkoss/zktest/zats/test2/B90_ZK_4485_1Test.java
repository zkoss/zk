/* B90_ZK_4485_1Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 30 12:33:43 CST 2024, Created by jameschu

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
@ForkJVMTestOnly
public class B90_ZK_4485_1Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B90-ZK-4485-1-zk.xml");

	@Test
	public void test() {
		connect("/test2/B90-ZK-4485.zul");
		waitResponse();
		click(jq("@button"));
		waitResponse();
		assertEquals("true", getZKLog());
	}
}
