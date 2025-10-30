/* F96_ZK_4771Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Mar 17 15:02:05 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

@ForkJVMTestOnly
public class F96_ZK_4771Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F96-ZK-4771-zk.xml");

	@Test
	public void test() {
		connect();
		JQuery button = jq("@button");
		click(button);
		waitResponse();
		checkStyleSheet("F96-ZK-4771-Style.css");


		click(jq(".z-a:contains(Atlantic)"));
		waitResponse();
		click(button);
		waitResponse();
		checkStyleSheet("F96-ZK-4771-Style2.css");

		click(jq(".z-a:contains(Default)"));
		waitResponse();
	}

	private void checkStyleSheet(String testFileName) {
		String[] log = getZKLog().split("\n");
		int len = log.length;
		int wcsIndex = -1, tabletIndex = -1, testStyleIndex = -1;
		for (int i = 0; i < len; i++) {
			String line = log[i];
			if (line.contains("zk.wcs"))
				wcsIndex = i;
			else if (line.contains("tablet.css"))
				tabletIndex = i;
			else if (line.contains(testFileName))
				testStyleIndex = i;
		}
		assertNotEquals(-1, wcsIndex, "zk.wcs should be loaded");
		assertNotEquals(-1, tabletIndex, "tablet.css should be loaded");
		assertNotEquals(-1, testStyleIndex, testFileName + " should be loaded");

		if ("F96-ZK-4771-Style2.css".equals(testFileName)) {
			MatcherAssert.assertThat(
				"F96-ZK-4771-Style2.css should loaded before tabletIndex",
				tabletIndex, greaterThan(testStyleIndex));
			MatcherAssert.assertThat(
				"F96-ZK-4771-Style2.css should loaded after zk.wcs",
				testStyleIndex, greaterThan(wcsIndex));
		}
		closeZKLog();
	}
}
