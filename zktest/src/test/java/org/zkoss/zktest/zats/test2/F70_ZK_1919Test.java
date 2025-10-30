/* F70_ZK_1919Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 02 18:07:57 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@ForkJVMTestOnly
public class F70_ZK_1919Test extends WebDriverTestCase {
	@RegisterExtension
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
		assertEquals(uuidPage, widget(jq("@page")).uuid());
		assertEquals(uuidWindow, widget(jq("@window")).uuid());
		assertEquals(uuidButton, widget(jq("@button")).uuid());
		assertEquals(uuidLabel, widget(jq("@label")).uuid());
	}
}
