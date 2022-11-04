package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00878WrongValueException2Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery focus = jq("@button");
		//bandbox
		JQuery l = jq("$l1");
		JQuery inp = jq("$inp1").find("input");
		assertEquals("", l.text());
		type(inp, "A");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("A", l.text());
		//should check error message
		type(inp, "");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("A", l.text());
		//should check error message
		type(inp, "B");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("B", l.text());
		//should check error message

		//combobox
		l = jq("$l2");
		inp = jq("$inp2").find("input");
		assertEquals("", l.text());
		type(inp, "A");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("A", l.text());
		//should check error message
		type(inp, "");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("A", l.text());
		//should check error message
		type(inp, "B");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("B", l.text());
		//should check error message

		//textbox
		l = jq("$l10");
		inp = jq("$inp10");
		assertEquals("", l.text());
		type(inp, "A");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("A", l.text());
		//should check error message
		type(inp, "");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("A", l.text());
		//should check error message
		type(inp, "B");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("B", l.text());
		//should check error message

		//decimalbox
		l = jq("$l4");
		inp = jq("$inp4");
		assertEquals("", l.text());
		type(inp, "1");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1.0", l.text());
		//should check error message
		sendKeys(inp, Keys.HOME, "-");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1.0", l.text());
		//should check error message
		type(inp, "2");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("2.0", l.text());
		//should check error message

		//doublebox
		l = jq("$l5");
		inp = jq("$inp5");
		assertEquals("", l.text());
		type(inp, "1");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1.0", l.text());
		//should check error message
		sendKeys(inp, Keys.HOME, "-");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1.0", l.text());
		//should check error message
		type(inp, "2");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("2.0", l.text());
		//should check error message

		//doublespinner
		l = jq("$l6");
		inp = jq("$inp6").find("input");
		assertEquals("", l.text());
		type(inp, "1");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1.0", l.text());
		//should check error message
		sendKeys(inp, Keys.HOME, "-");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1.0", l.text());
		//should check error message
		type(inp, "2");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("2.0", l.text());
		//should check error message

		//intbox
		l = jq("$l7");
		inp = jq("$inp7");
		assertEquals("", l.text());
		type(inp, "1");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1", l.text());
		//should check error message
		sendKeys(inp, Keys.HOME, "-");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1", l.text());
		//should check error message
		type(inp, "2");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("2", l.text());
		//should check error message

		//longbox
		l = jq("$l8");
		inp = jq("$inp8");
		assertEquals("", l.text());
		type(inp, "1");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1", l.text());
		//should check error message
		sendKeys(inp, Keys.HOME, "-");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1", l.text());
		//should check error message
		type(inp, "2");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("2", l.text());
		//should check error message

		//spinner
		l = jq("$l9");
		inp = jq("$inp9").find("input");
		assertEquals("", l.text());
		type(inp, "1");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1", l.text());
		//should check error message
		sendKeys(inp, Keys.HOME, "-");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("1", l.text());
		//should check error message
		type(inp, "2");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("2", l.text());
		//should check error message

		//datebox
		l = jq("$l3");
		inp = jq("$inp3").find("input");
		assertEquals("", l.text());
		type(inp, "20120223");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("20120223", l.text());
		//should check error message
		sendKeys(inp, Keys.END, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE, "10101");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("20120223", l.text());
		//should check error message
		type(inp, "20120223");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("20120223", l.text());
		//should check error message

		//timebox
		l = jq("$l11");
		inp = jq("$inp11").find("input");
		assertEquals("", l.text());
		sendKeys(inp, Keys.HOME, "1300");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("13:00", l.text());
		//should check error message
		sendKeys(inp, Keys.HOME, "1000");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("13:00", l.text());
		//should check error message
		sendKeys(inp, Keys.HOME, "1400");
		waitResponse();
		click(focus);
		waitResponse();
		assertEquals("14:00", l.text());
		//should check error message
		
		/*
	      var errorPopup = jq(".z-errorbox")
	      verifyEquals(0, errorPopup.length())
	      verifyEquals(1, errorPopup.length())
	      verifyEquals(0, errorPopup.length())
        */
	}
}
