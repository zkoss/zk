/* F96_ZK_4771Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Mar 17 15:02:05 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.greaterThan;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F96_ZK_4771Test extends WebDriverTestCase {
	@ClassRule
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
		Assert.assertNotEquals("zk.wcs should be loaded", -1, wcsIndex);
		Assert.assertNotEquals("tablet.css should be loaded", -1, tabletIndex);
		Assert.assertNotEquals(testFileName + " should be loaded", -1, testStyleIndex);

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
