/* B90_ZK_4485Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 13 10:03:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
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
public class B90_ZK_4485Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B90-ZK-4485-zk.xml");

	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertEquals("true", getZKLog());
	}
}
