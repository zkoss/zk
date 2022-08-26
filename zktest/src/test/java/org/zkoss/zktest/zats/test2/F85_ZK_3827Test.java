package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author bob peng
 */
public class F85_ZK_3827Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery msg = jq(".z-combobox-empty-search-message");

		String typeString = "a";
		type(jq("@combobox .z-combobox-input"), typeString);
		waitResponse();
		assertFalse(msg.isVisible(),
				"Error: EmptySearchMessage should disappear");

		typeString = "g";
		type(jq("@combobox .z-combobox-input"), typeString);
		waitResponse();
		assertTrue(msg.isVisible(), "Error: EmptySearchMessage should appear");
	}
}