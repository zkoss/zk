package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author bob peng
 */
public class B85_ZK_3818Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		// 4 sections
		for (int i = 1; i <= 4; i++) {
			JQuery btnAdd = jq("$btnAdd" + i);
			JQuery btnDelete = jq("$btnDelete" + i);

			// click add button
			click(btnAdd);
			waitResponse();

			// select radio
			click(jq("$win" + i + " [type=radio]:eq(0)"));
			waitResponse();

			// click add button
			for (int j = 0; j < 2; j++) {
				click(btnAdd);
				waitResponse();
			}

			// click delete button
			for (int j = 0; j < 2; j++) {
				click(btnDelete);
				waitResponse();
			}
		}

		String s = "selected index: -1\n" +
				"selected index: 0\n" +
				"selected index: 1\n" +
				"selected index: 2\n" +
				"selected index: 1\n" +
				"selected index: 0\n" +
				"selected index: -1\n" +
				"selected index: 0\n" +
				"selected index: 1\n" +
				"selected index: 2\n" +
				"selected index: 1\n" +
				"selected index: 0\n" +
				"selected index: -1\n" +
				"selected index: 0\n" +
				"selected index: 1\n" +
				"selected index: 2\n" +
				"selected index: 1\n" +
				"selected index: 0\n" +
				"selected index: -1\n" +
				"selected index: 0\n" +
				"selected index: 1\n" +
				"selected index: 2\n" +
				"selected index: 1\n" +
				"selected index: 0";

		Assert.assertEquals("Error: Wrong selected index.", s, getZKLog().trim());
	}
}