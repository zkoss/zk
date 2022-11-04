package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F00889ChildrenBindingConverterTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		JQuery item1 = jq("$item1");
		JQuery set1 = jq("$set1");
		JQuery list1 = jq("$list1");
		JQuery array1 = jq("$array1");
		JQuery enum1 = jq("$enum1");
		JQuery item2 = jq("$item2");
		JQuery set2 = jq("$set2");
		JQuery list2 = jq("$list2");
		JQuery array2 = jq("$array2");
		JQuery enum2 = jq("$enum2");

		JQuery ls = item1.find("@label");
		assertEquals(1, ls.length());
		assertEquals("A", ls.eq(0).text());

		ls = set1.find("@label");
		assertEquals(3, ls.length());

		ls = list1.find("@label");
		assertEquals(3, ls.length());
		assertEquals("A", ls.eq(0).text());
		assertEquals("B", ls.eq(1).text());
		assertEquals("C", ls.eq(2).text());

		ls = array1.find("@label");
		assertEquals(3, ls.length());
		assertEquals("A", ls.eq(0).text());
		assertEquals("B", ls.eq(1).text());
		assertEquals("C", ls.eq(2).text());

		ls = enum1.find("@label");
		assertEquals(3, ls.length());
		assertEquals("A", ls.eq(0).text());
		assertEquals("B", ls.eq(1).text());
		assertEquals("C", ls.eq(2).text());

		ls = item2.find("@label");
		assertEquals(1, ls.length());
		assertEquals("D", ls.eq(0).text());

		ls = set2.find("@label");
		assertEquals(3, ls.length());

		ls = list2.find("@label");
		assertEquals(3, ls.length());
		assertEquals("D", ls.eq(0).text());
		assertEquals("E", ls.eq(1).text());
		assertEquals("F", ls.eq(2).text());

		ls = array2.find("@label");
		assertEquals(3, ls.length());
		assertEquals("D", ls.eq(0).text());
		assertEquals("E", ls.eq(1).text());
		assertEquals("F", ls.eq(2).text());

		ls = enum2.find("@label");
		assertEquals(3, ls.length());
		assertEquals("D", ls.eq(0).text());
		assertEquals("E", ls.eq(1).text());
		assertEquals("F", ls.eq(2).text());
	}
}
