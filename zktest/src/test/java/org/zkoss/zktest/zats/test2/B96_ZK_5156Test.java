package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class B96_ZK_5156Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		assertEquals("123456789.02", jq("$decimal-1").toElement().get("value"));
		assertEquals("12,34,56,789.02", jq("$decimal-2").toElement().get("value"));
		assertEquals("12,34,56,789.02", jq("$decimal-3").toElement().get("value"));
	}
}
