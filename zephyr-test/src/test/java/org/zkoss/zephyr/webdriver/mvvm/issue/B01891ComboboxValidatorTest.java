package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01891ComboboxValidatorTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery cb1 = jq("$cb1");
		JQuery lb11 = jq("$lb11");
		JQuery lb12 = jq("$lb12");
		JQuery cb2 = jq("$cb2");
		JQuery lb21 = jq("$lb21");
		JQuery lb22 = jq("$lb22");

		click(cb1.find(".z-combobox-button"));
		waitResponse();
		click(cb1.find("@comboitem").eq(0));
		waitResponse();
		assertEquals("01", cb1.find("input").val());
		assertEquals("", lb11.text());
		assertEquals("01", lb12.text());

		sendKeys(cb1.find("input"), Keys.END, Keys.BACK_SPACE, Keys.TAB);
		waitResponse();
		assertEquals("0", cb1.find("input").val());
		assertEquals("Please select an item!!", lb11.text());
		assertEquals("01", lb12.text());

		sendKeys(cb1.find("input"), Keys.BACK_SPACE, Keys.TAB);
		waitResponse();
		assertEquals("", cb1.find("input").val());
		assertEquals("Please select an item!!", lb11.text());
		assertEquals("01", lb12.text());

		click(cb2.find(".z-combobox-button"));
		waitResponse();
		click(cb2.find("@comboitem").eq(0));
		waitResponse();
		waitResponse();
		assertEquals("01", cb2.find("input").val());
		assertEquals("", lb21.text());
		assertEquals("0", lb22.text());

		sendKeys(cb2.find("input"), Keys.END, Keys.BACK_SPACE, Keys.TAB);
		waitResponse();
		assertEquals("0", cb2.find("input").val());
		assertEquals("Please select an item!!", lb21.text());
		assertEquals("0", lb22.text());

		sendKeys(cb2.find("input"), Keys.BACK_SPACE, Keys.TAB);
		waitResponse();
		assertEquals("", cb2.find("input").val());
		assertEquals("Please select an item!!", lb21.text());
		assertEquals("0", lb22.text());
	}
}
