/* F70_ZK_1919Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 02 18:07:57 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@Category(ForkJVMTestOnly.class)
public class F70_ZK_1919Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml(F70_ZK_1919Test.class);

	@Test
	public void test() {
		connect();

		String uuidPage = widget(jq("@page")).uuid();
		String uuidWindow = widget(jq("@window")).uuid();
		String uuidButton = widget(jq("@button")).uuid();
		String uuidLabel = widget(jq("@label")).uuid();

		driver.navigate().refresh();
		waitResponse();
		checkUuid(uuidPage, uuidWindow, uuidButton, uuidLabel);

		driver.navigate().refresh();
		waitResponse();
		checkUuid(uuidPage, uuidWindow, uuidButton, uuidLabel);
	}

	private void checkUuid(String uuidPage, String uuidWindow, String uuidButton, String uuidLabel) {
		Assert.assertEquals(uuidPage, widget(jq("@page")).uuid());
		Assert.assertEquals(uuidWindow, widget(jq("@window")).uuid());
		Assert.assertEquals(uuidButton, widget(jq("@button")).uuid());
		Assert.assertEquals(uuidLabel, widget(jq("@label")).uuid());
	}
}
