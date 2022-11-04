package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00995ValidationMessagesTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery l1 = jq("$l1");
		JQuery t1 = jq("$t1");
		JQuery msg11 = jq("$msg11");
		JQuery msg12 = jq("$msg12");
		JQuery msg13 = jq("$msg13");
		JQuery msg21 = jq("$msg21");
		JQuery msg22 = jq("$msg22");
		JQuery msg31 = jq("$msg31");
		JQuery msg32 = jq("$msg32");
		JQuery msg33 = jq("$msg33");
		JQuery msg34 = jq("$msg34");
		JQuery msg41 = jq("$msg41");
		JQuery msg42 = jq("$msg42");
		JQuery msg43 = jq("$msg43");
		JQuery msg44 = jq("$msg44");
		JQuery msggrid1 = jq("$msggrid1");
		JQuery msggrid2 = jq("$msggrid2");
		JQuery msggrid3 = jq("$msggrid3");

		assertEquals("ABC", l1.text());
		assertEquals("ABC", t1.val());
		assertEquals("", msg11.text());
		assertEquals("", msg12.text());
		assertEquals("", msg13.text());
		assertEquals("", msg21.text());
		assertEquals("", msg22.text());
		assertEquals("true", msg31.text());
		assertEquals("true", msg32.text());
		assertEquals("false", msg33.text());
		assertEquals("false", msg34.text());
		assertEquals("", msg41.text());
		assertEquals("", msg42.text());
		assertEquals("", msg43.text());
		assertEquals("", msg44.text());
		assertEquals(0, msggrid1.find("@row").length());
		assertEquals(0, msggrid2.find("@row").length());
		assertEquals(0, msggrid3.find("@row").length());

		type(t1, "ABCa");
		waitResponse();
		assertEquals("ABC", l1.text());
		assertEquals("ABCa", t1.val());
		assertEquals("[1] value must equals ignore case 'abc', but is ABCa", msg11.text());
		assertEquals("[1] value must equals ignore case 'abc', but is ABCa", msg12.text());
		assertEquals("[1] value must equals ignore case 'abc', but is ABCa", msg13.text());
		assertEquals("", msg21.text());
		assertEquals("", msg22.text());
		assertEquals("false", msg31.text());
		assertEquals("false", msg32.text());
		assertEquals("true", msg33.text());
		assertEquals("true", msg34.text());
		assertEquals("[2] value must equals ignore case 'abc', but is ABCa", msg41.text());
		assertEquals("[2] value must equals ignore case 'abc', but is ABCa", msg42.text());
		assertEquals("[2] value must equals ignore case 'abc', but is ABCa", msg43.text());
		assertEquals("[2] value must equals ignore case 'abc', but is ABCa", msg44.text());
		assertEquals(3, msggrid1.find("@row").length());
		assertEquals(3, msggrid2.find("@row").length());
		assertEquals(3, msggrid3.find("@row").length());

		int i = 1;
		JQuery rows = msggrid1.find("@row");
		for (int j = 0; j < rows.length(); j++) {
			JQuery row = rows.eq(j);
			assertEquals("[" + i + "] value must equals ignore case 'abc', but is ABCa", row.find("@label").text());
			i++;
		}

		i = 1;
		rows = msggrid2.find("@row");
		for (int j = 0; j < rows.length(); j++) {
			JQuery row = rows.eq(j);
			assertEquals("[" + i + "] value must equals ignore case 'abc', but is ABCa", row.find("@label").text());
			i++;
		}

		i = 1;
		rows = msggrid3.find("@row");
		for (int j = 0; j < rows.length(); j++) {
			JQuery row = rows.eq(j);
			assertEquals("[" + i + "] value must equals ignore case 'abc', but is ABCa", row.find("@label").text());
			i++;
		}
	}
}
