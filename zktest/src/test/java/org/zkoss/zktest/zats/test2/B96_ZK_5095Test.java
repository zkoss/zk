/* B96_ZK_5095Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 28 14:45:46 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
@ForkJVMTestOnly
public class B96_ZK_5095Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B96-ZK-5095.xml");

	@Test
	public void test() {
		connect();
		click(jq("$serializeBtn"));
		waitResponse();
		assertFalse(jq("span:contains(java.io.NotSerializableException)").exists());
	}
}