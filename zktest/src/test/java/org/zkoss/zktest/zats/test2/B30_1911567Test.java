package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B30_1911567Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		//Name header
		JQuery name = jq("@listheader:eq(0)");
		//Gender header
		JQuery gender = jq("@listheader:eq(1)");

		//Verify name header exists
		assertTrue(name.exists());

		//Verify gender header exists
		assertTrue(gender.exists());

		String nameText = jq("@listheader:eq(0)").text().trim();
		assertEquals(nameText, "name");

		String genderText = jq("@listheader:eq(1)").text().trim();
		assertEquals(genderText, "gender");
	}
}