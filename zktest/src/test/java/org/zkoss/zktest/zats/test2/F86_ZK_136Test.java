/* F86_ZK_136Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 24 12:34:03 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F86_ZK_136Test extends WebDriverTestCase {
	private final static String TRUE = "true";
	private final static String FALSE = "false";
	private JQuery isIndeterminate;
	private JQuery isChecked;

	@Test
	public void test() {
		connect();
		isIndeterminate = findButtonByContent("isIndeterminate");
		isChecked = findButtonByContent("isChecked");
		JQuery toggleIndeterminate = findButtonByContent("toggleIndeterminate");
		JQuery checkbox = jq(".z-checkbox");

		verifyLogs(TRUE, TRUE);

		click(toggleIndeterminate);
		waitResponse();
		verifyLogs(FALSE, TRUE);

		click(toggleIndeterminate);
		waitResponse();
		verifyLogs(TRUE, TRUE);

		click(checkbox);
		waitResponse();
		verifyLogs(FALSE, FALSE);

		click(toggleIndeterminate);
		waitResponse();
		verifyLogs(TRUE, FALSE);

		click(checkbox);
		waitResponse();
		verifyLogs(FALSE, TRUE);
	}

	private JQuery findButtonByContent(String content) {
		return jq(".z-button:contains(" + content + ")");
	}

	private void verifyLogs(String isIndeterminateLog, String isCheckedLog) {
		clickAndVerifyLog(isIndeterminate, isIndeterminateLog);
		clickAndVerifyLog(isChecked, isCheckedLog);
	}

	private void clickAndVerifyLog(JQuery target, String expectedLog) {
		click(target);
		waitResponse();
		Assert.assertEquals(expectedLog, getZKLog());
		closeZKLog();
		waitResponse();
	}
}
