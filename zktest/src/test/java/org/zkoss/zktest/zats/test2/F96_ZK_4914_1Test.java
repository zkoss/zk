/* F96_ZK_4914_1Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 29 14:23:33 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
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
public class F96_ZK_4914_1Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F96-ZK-4914-zk-1.xml");

	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals("null", getEval("zk.scriptErrorHandler"));
	}
}
