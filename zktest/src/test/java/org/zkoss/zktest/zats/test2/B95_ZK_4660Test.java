/* B95_ZK_4660Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Oct 08 11:49:35 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B95_ZK_4660Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 0; i < 4; i++) {
			JQuery logButton = jq("@button").eq(i);
			// log size
			click(logButton);
			waitResponse();
			// log size after maximize
			click(jq(".z-icon-expand").eq(i));
			waitResponse();
			click(logButton);
			waitResponse();
			// log size after restore
			click(jq(".z-icon-compress"));
			waitResponse();
			click(logButton);
			waitResponse();

			checkSizeLog();
		}
	}

	private void checkSizeLog() {
		String[] sizeLog = getZKLog().split("\n");
		Assertions.assertEquals(sizeLog[0], sizeLog[1], "size shouldn't change after maximize");
		Assertions.assertEquals(sizeLog[0], sizeLog[2], "size shouldn't change after restore");
		closeZKLog();
	}
}
