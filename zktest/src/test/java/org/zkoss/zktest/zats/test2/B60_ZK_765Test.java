/* B60_ZK_765Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 04 14:55:30 CST 2024, Created by jamson

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

@ForkJVMTestOnly
public class B60_ZK_765Test extends WebDriverTestCase {

	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}

	@Test
	public void test() {
		connect();

		Widget btnOne = jq("@button:eq(0)").toWidget(),
				btnTwo = jq("@button:eq(1)").toWidget(),
				btnThree = jq("@button:eq(2)").toWidget();

		clickAndWait(btnTwo);
		assertTrue(jq("$msg:contains(selected index is undefined, no selection)").exists()
						|| jq("$msg:contains(selected index is -1)").exists(),
				"message should be 'selected index is undefined, no selection or ... index is -1 ...'");
		clickAndWait(jq(".z-listitem:contains(Item 1)"));
		clickAndWait(btnTwo);
		assertTrue(jq("$msg:contains(selected index is 1, widget selected is true)").exists(), "message should be 'selected index is 1, widget selected is true'");
		clickAndWait(btnOne);
		clickAndWait(btnTwo);
		assertTrue(jq("$msg:contains(selected index is 1, widget selected is true)").exists(), "message should be 'selected index is 1, widget selected is true'");

		waitResponse();
		driver.navigate().refresh();
		waitResponse();

		clickAndWait(btnThree);
		assertTrue(jq("$msg:contains(selected no selection)").exists(), "message should be 'selected no selection'");
		clickAndWait(jq(".z-listitem:contains(Item 1)"));
		clickAndWait(btnThree);
		assertTrue(jq("$msg:contains(selected index is 1)").exists(), "message should be 'selected index is 1'");
		clickAndWait(btnOne);
		clickAndWait(btnThree);
		assertTrue(jq("$msg:contains(selected index is 1)").exists(), "message should be 'selected index is 1'");
	}

	private void clickAndWait(Widget wgt) {
		click(wgt);
		waitResponse();
	}

	private void clickAndWait(JQuery jq) {
		clickAndWait(jq.toWidget());
	}
}
