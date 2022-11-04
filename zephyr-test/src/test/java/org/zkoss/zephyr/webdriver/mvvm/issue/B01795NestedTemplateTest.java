package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01795NestedTemplateTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery box1s = jq("@grid @label");
		assertEquals("[AJAX]", box1s.eq(0).text());
		assertEquals("[AJAX]", box1s.eq(1).text());
		assertEquals("[AJAX]", box1s.eq(2).text());
		assertEquals("[Java, C]", box1s.eq(3).text());
		assertEquals("[Java, C]", box1s.eq(4).text());
		assertEquals("[Java, C]", box1s.eq(5).text());
	}
}
