/* B96_ZK_5251Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 11 12:32:44 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.TouchWebDriverTestCase;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
@Category(ForkJVMTestOnly.class)
public class B96_ZK_5251Test extends TouchWebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Test
	public void test() {
		connect();
		waitResponse();
		Assert.assertFalse(hasError());
	}
}
