/* B96_ZK_5251Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 11 12:32:44 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.TouchWebDriverTestCase;

/**
 * @author jameschu
 */
@ForkJVMTestOnly
public class B96_ZK_5251Test extends TouchWebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Test
	public void test() {
		connect();
		waitResponse();
		assertFalse(hasError());
	}
}
