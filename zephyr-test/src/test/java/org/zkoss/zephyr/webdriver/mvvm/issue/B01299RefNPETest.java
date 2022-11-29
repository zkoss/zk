package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B01299RefNPETest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		type(jq("$win1 $tba1"), "AA");
		waitResponse();
		assertEquals("AA", jq("$win1 $lba1").text());

		type(jq("$win2 $tba2"), "BB");
		waitResponse();
		assertEquals("BB", jq("$win2 $lba2").text());

		type(jq("$win3 $tba3"), "CC");
		waitResponse();
		assertEquals("CC", jq("$win3 $lba31").text());
		assertEquals("CC", jq("$win3 $lba32").text());
		assertEquals("CC", jq("$win3 $lba33").text());
		assertEquals("CC", jq("$win3 $lba34").text());

		type(jq("$win4 $tbb1"), "D");
		waitResponse();
		click(jq("$win4 $btnb1"));
		waitResponse();
		assertEquals("D", jq("$win4 $lbb1").text());

		type(jq("$win5 $tbb2"), "E");
		waitResponse();
		click(jq("$win5 $btnb2"));
		waitResponse();
		assertEquals("E", jq("$win5 $lbb2").text());

		type(jq("$win6 $tbb3"), "F");
		waitResponse();
		click(jq("$win6 $btnb3"));
		waitResponse();
		assertEquals("F", jq("$win6 $lbb3").text());

		type(jq("$win7 $tbb4"), "G");
		waitResponse();
		click(jq("$win7 $btnb4"));
		waitResponse();
		assertEquals("G", jq("$win7 $lbb41").text());
		assertEquals("G", jq("$win7 $lbb42").text());
		assertEquals("G", jq("$win7 $lbb43").text());
		assertEquals("G", jq("$win7 $lbb44").text());

		type(jq("$win8 $tbb5"), "H");
		waitResponse();
		click(jq("$win8 $btnb5"));
		waitResponse();
		assertEquals("H", jq("$win8 $lbb5").text());

		type(jq("$win9 $tbb6"), "I");
		waitResponse();
		click(jq("$win9 $btnb6"));
		waitResponse();
		assertEquals("I", jq("$win9 $lbb61").text());
		assertEquals("I", jq("$win9 $lbb62").text());
		assertEquals("I", jq("$win9 $lbb63").text());
		assertEquals("I", jq("$win9 $lbb64").text());

		type(jq("$win10 $tbc1"), "J");
		waitResponse();
		assertTrue(hasError());

		type(jq("$win11 $tbc2"), "K");
		waitResponse();
		click(jq("$win11 $btnc2"));
		waitResponse();
		assertTrue(hasError());
	}
}
