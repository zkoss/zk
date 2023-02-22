package org.zkoss.zephyr.webdriver.mvvm.book.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

public class SelectboxmodelselectionTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/basic/selectboxmodelselection.zul");
		waitResponse();
		Widget sb1 = jq("$sb1").toWidget();
		Widget sb2 = jq("$sb2").toWidget();
		Widget sb3 = jq("$sb3").toWidget();
		Widget msg = jq("$msg").toWidget();
		Widget btn1 = jq("$btn1").toWidget();
		Widget btn2 = jq("$btn2").toWidget();
		assertEquals("", msg.get("value"));
		assertEquals("-1", sb1.get("selectedIndex"));
		assertEquals("-1", sb2.get("selectedIndex"));
		assertEquals("1", sb3.get("selectedIndex"));
		click(jq(sb1));
		click(jq(sb1).find("option").get(0));
		waitResponse();
		click(jq(sb2));
		click(jq(sb2).find("option").get(1));
		waitResponse();
		click(jq(sb3));
		click(jq(sb3).find("option").get(2));
		waitResponse();
		assertEquals("0", sb1.get("selectedIndex"));
		assertEquals("1", sb2.get("selectedIndex"));
		assertEquals("2", sb3.get("selectedIndex"));
		click(btn1);
		waitResponse();
		assertEquals("0", sb1.get("selectedIndex"));
		assertEquals("1", sb2.get("selectedIndex"));
		assertEquals("2", sb3.get("selectedIndex"));
		click(btn2);
		waitResponse();
		assertEquals("0", sb1.get("selectedIndex"));
		assertEquals("1", sb2.get("selectedIndex"));
		assertEquals("3", sb3.get("selectedIndex"));
	}
}