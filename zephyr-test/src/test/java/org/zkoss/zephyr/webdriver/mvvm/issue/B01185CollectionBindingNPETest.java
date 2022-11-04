package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01185CollectionBindingNPETest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$addPersonBtn"));
		waitResponse();
		click(jq("$delPerson_0"));
		waitResponse();
		click(jq("$addUrlBtn"));
		waitResponse();
		click(jq("$delUrl_0"));
		waitResponse();

		JQuery widget = jq("$delUrl_0");
		assertFalse(widget.length() > 0);
	}
}
