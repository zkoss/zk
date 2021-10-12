/* B95_ZK_4742Test.java

		Purpose:
				
		Description:
				
		History:
				Wed Dec 30 16:27:44 CST 2020, Created by katherinelin

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;

@Category(ForkJVMTestOnly.class)
public class B95_ZK_4742Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B95_ZK_4742Test.class);

	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Assert.assertTrue(getWebDriver().getCurrentUrl().endsWith("B95-ZK-4742-login.zul"));
	}
}
