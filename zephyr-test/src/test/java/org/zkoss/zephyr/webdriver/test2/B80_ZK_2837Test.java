package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

public class B80_ZK_2837Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		checkMessage(new String[]{"Before", "Details:", "Detail: initial detail", "After"});
		JQuery btn1 = jq("@button").eq(0);
		JQuery btn2 = jq("@button").eq(1);
		click(btn1);
		waitResponse();
		checkMessage(new String[]{"Before", "Details:", "Detail: new detail", "After"});
		click(btn1);
		waitResponse();
		assertNoAnyError();
		click(btn2);
		waitResponse();
		checkMessage(new String[]{"Before", "After"});
		click(btn1);
		waitResponse();
		checkMessage(new String[]{"Before", "Details:", "Detail: new detail", "After"});
	}

	private void checkMessage(String[] messages) {
		Widget child = jq("$host").toWidget().firstChild();
		if (messages.length < 3) {
			for (int i = 0, length = messages.length; i < length; i++) {
				assertEquals(messages[i], jq(child).find("@label").text().trim());
				child = child.nextSibling();
			}
		} else {
			assertEquals(messages[0], jq(child).find("@label").text().trim());
			child = child.nextSibling();
			assertEquals(messages[1], jq(child).find("@label").eq(0).text().trim());
			assertEquals(messages[2], jq(child).find("@label").eq(1).text().trim());
			child = child.nextSibling();
			assertEquals(messages[3], jq(child).find("@label").text().trim());
		}
	}
}
