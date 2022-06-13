/* B96_ZK_5095Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 28 14:45:46 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertFalse;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
@Category(ForkJVMTestOnly.class)
public class B96_ZK_5095Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/B96-ZK-5095.xml");

	@Test
	public void test() {
		connect();
		click(jq("$serializeBtn"));
		waitResponse();
		assertFalse(jq("span:contains(java.io.NotSerializableException)").exists());
	}
}