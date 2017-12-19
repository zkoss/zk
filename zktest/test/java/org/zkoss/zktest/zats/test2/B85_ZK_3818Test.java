package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertFalse;

/**
 * @author bob peng
 */
public class B85_ZK_3818Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btnAdd = jq("$btnAdd");
		JQuery btnDelete = jq("$btnDelete");

		// click Add button three times
		for (int i = 0; i < 3; i++) {
			click(btnAdd);
			waitResponse();
		}

		// select the third radio
		click(jq("[type=radio]:eq(2)"));
		waitResponse();

		// click Delete button
		click(btnDelete);
		waitResponse();

		// select the first radio
		click(jq("[type=radio]:eq(0)"));
		waitResponse();

		assertFalse("error popped", jq(".z-messagebox-error").exists());
	}
}